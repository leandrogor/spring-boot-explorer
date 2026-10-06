import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { 
  ScenarioInfo, SourceCodeResponse, ConceptInfo, 
  ArchitectureLayer, TraceData, Product, PageResponse, 
  Category, Order 
} from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class ExplorerService {
  private http = inject(HttpClient);
  private baseUrl = '/api/explorer';
  private apiBaseUrl = '/api';

  getScenarios(): Observable<ScenarioInfo[]> {
    return this.http.get<ScenarioInfo[]>(`${this.baseUrl}/scenarios`).pipe(
      catchError(() => of([]))
    );
  }

  getScenario(id: string): Observable<ScenarioInfo | undefined> {
    return this.getScenarios().pipe(
      map(scenarios => scenarios.find(s => s.id === id))
    );
  }

  getSource(file: string, startLine?: number, endLine?: number): Observable<SourceCodeResponse> {
    let url = `${this.baseUrl}/source?file=${encodeURIComponent(file)}`;
    if (startLine) url += `&startLine=${startLine}`;
    if (endLine) url += `&endLine=${endLine}`;
    return this.http.get<SourceCodeResponse>(url);
  }

  getConcepts(): Observable<ConceptInfo[]> {
    return this.http.get<ConceptInfo[]>(`${this.baseUrl}/concepts`).pipe(
      catchError(() => of([]))
    );
  }

  getArchitecture(): Observable<ArchitectureLayer[]> {
    return this.http.get<ArchitectureLayer[]>(`${this.baseUrl}/architecture`).pipe(
      catchError(() => of([]))
    );
  }

  getTrace(traceId: string): Observable<TraceData> {
    return this.http.get<TraceData>(`/api/trace/${traceId}`);
  }

  getProducts(): Observable<PageResponse<Product>> {
    return this.http.get<PageResponse<Product>>(`${this.apiBaseUrl}/products`).pipe(
      catchError(() => of({ content: [], totalElements: 0, totalPages: 0, size: 0, page: 0 }))
    );
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiBaseUrl}/categories`).pipe(
      catchError(() => of([]))
    );
  }

  getOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiBaseUrl}/orders`).pipe(
      catchError(() => of([]))
    );
  }

  resetData(): Observable<any> {
    return this.http.post(`/api/data/reset`, {});
  }

  executeCustomRequest(method: string, url: string, headers: Record<string, string>, body: any): Observable<{ status: number, statusText: string, headers: Record<string, string>, body: any, durationMs: number, traceId?: string }> {
    const startTime = Date.now();
    let httpHeaders = new HttpHeaders();
    
    for (const [key, value] of Object.entries(headers)) {
      if (key.toLowerCase() !== 'content-type' || value !== 'application/json') {
        httpHeaders = httpHeaders.set(key, value);
      }
    }
    
    const requestOptions = {
      headers: httpHeaders,
      observe: 'response' as const,
      responseType: 'text' as const
    };

    let requestObs: Observable<any>;
    const m = method.toUpperCase();
    if (m === 'GET') {
      requestObs = this.http.get(url, requestOptions);
    } else if (m === 'POST') {
      requestObs = this.http.post(url, body, requestOptions);
    } else if (m === 'PUT') {
      requestObs = this.http.put(url, body, requestOptions);
    } else if (m === 'PATCH') {
      requestObs = this.http.patch(url, body, requestOptions);
    } else if (m === 'DELETE') {
      requestObs = this.http.delete(url, requestOptions);
    } else {
      requestObs = this.http.get(url, requestOptions);
    }

    return requestObs.pipe(
      map(response => {
        const durationMs = Date.now() - startTime;
        let responseBody = response.body;
        try {
          responseBody = response.body ? JSON.parse(response.body) : null;
        } catch(e) {}
        
        const resHeaders: Record<string, string> = {};
        response.headers.keys().forEach((k: string) => {
          resHeaders[k] = response.headers.get(k) || '';
        });

        return {
          status: response.status,
          statusText: response.statusText,
          headers: resHeaders,
          body: responseBody,
          durationMs,
          traceId: response.headers.get('x-trace-id') || response.headers.get('X-Trace-Id') || undefined
        };
      }),
      catchError(error => {
        const durationMs = Date.now() - startTime;
        let responseBody = error.error;
        if (typeof responseBody === 'string') {
          try {
            responseBody = JSON.parse(responseBody);
          } catch(e) {}
        }
        
        const resHeaders: Record<string, string> = {};
        if (error.headers) {
          error.headers.keys().forEach((k: string) => {
            resHeaders[k] = error.headers.get(k) || '';
          });
        }
        
        return of({
          status: error.status || 0,
          statusText: error.statusText || 'Unknown Error',
          headers: resHeaders,
          body: responseBody || error.message,
          durationMs,
          traceId: error.headers?.get('x-trace-id') || error.headers?.get('X-Trace-Id') || undefined
        });
      })
    );
  }
}
