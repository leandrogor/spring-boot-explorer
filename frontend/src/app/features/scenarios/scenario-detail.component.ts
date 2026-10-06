import { Component, inject, signal, ViewChild, OnInit, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ExplorerService } from '../../core/services/explorer.service';
import { AuthService } from '../../core/services/auth.service';
import { ScenarioInfo, ScenarioStep, TraceData, SourceRef } from '../../core/models/models';
import { CodeModalComponent } from '../../shared/components/code-modal.component';
import { TraceTimelineComponent } from '../../shared/components/trace-timeline.component';
import { BridgeCardComponent } from '../../shared/components/bridge-card.component';
import * as Prism from 'prismjs';
import 'prismjs/components/prism-json';

@Component({
  selector: 'app-scenario-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, CodeModalComponent, TraceTimelineComponent, BridgeCardComponent],
  template: `
    @if (scenario()) {
      <div class="detail-container">
        <div class="header-actions">
          <a routerLink="/scenarios" class="btn btn-outline btn-sm">← Volver a Escenarios</a>
        </div>
        
        <div class="scenario-header">
          <span class="scenario-badge">Escenario Guiado</span>
          <h1>{{ scenario()!.title }}</h1>
          <p class="business-story">{{ scenario()!.story }}</p>
          
          <div class="concepts-row">
            <span class="concepts-label">Conceptos:</span>
            <div class="tags">
              @for (concept of scenario()!.concepts; track concept) {
                <span class="badge badge-primary">{{ concept }}</span>
              }
            </div>
          </div>
        </div>

        @if (scenario()!.bridge) {
          <app-bridge-card [bridge]="scenario()!.bridge"></app-bridge-card>
        }

        @if (scenario()!.sourceRefs && scenario()!.sourceRefs.length > 0) {
          <div class="source-files-card">
            <h4>📄 Archivos de Código Fuente en este Escenario</h4>
            <div class="file-buttons">
              @for (ref of scenario()!.sourceRefs; track ref.file) {
                <button class="btn btn-outline btn-sm" (click)="openCode(ref.file, ref.startLine, ref.endLine)">
                  🔍 {{ ref.label || ref.file }}
                </button>
              }
            </div>
          </div>
        }

        <div class="interactive-runner">
          <div class="runner-header">
            <div class="runner-title">
              <h3>⚡ Panel "Probalo"</h3>
              <p>Ejecutá requests HTTP reales contra el backend de Spring Boot y explorá la respuesta y el ciclo de vida.</p>
            </div>
            
            @if (scenario()!.steps && scenario()!.steps.length > 0) {
              <div class="step-tabs">
                @for (step of scenario()!.steps; track step.stepNumber; let i = $index) {
                  <button class="step-tab-btn" 
                          [class.active]="selectedStepIndex() === i"
                          (click)="selectStep(i)">
                    Paso {{ step.stepNumber }}: {{ step.description }}
                  </button>
                }
              </div>
            }
          </div>

          <div class="runner-body">
            <!-- Left: Request Form -->
            <div class="panel request-panel">
              <div class="panel-title">Request HTTP</div>
              
              <div class="http-url-bar">
                <span class="method-badge" [ngClass]="requestMethod().toLowerCase()">{{ requestMethod() }}</span>
                <input type="text" [(ngModel)]="requestUrl" class="url-input" placeholder="/api/...">
              </div>

              <div class="form-group">
                <label>Headers (JSON):</label>
                <textarea [(ngModel)]="requestHeadersStr" rows="3" class="code-input font-mono"></textarea>
              </div>

              @if (requestMethod() !== 'GET' && requestMethod() !== 'DELETE') {
                <div class="form-group">
                  <label>Request Body (JSON):</label>
                  <textarea [(ngModel)]="requestBodyStr" rows="5" class="code-input font-mono"></textarea>
                </div>
              }

              <button class="btn btn-primary btn-execute" (click)="executeRequest()" [disabled]="executing()">
                {{ executing() ? '⏳ Ejecutando en Spring...' : '▶ Ejecutar Request' }}
              </button>
            </div>

            <!-- Right: Response Viewer -->
            <div class="panel response-panel">
              <div class="response-header">
                <div class="panel-title">Respuesta del Servidor</div>
                @if (responseStatus()) {
                  <div class="status-tags">
                    <span class="status-badge" [ngClass]="getStatusClass(responseStatus()!)">
                      {{ responseStatus() }} {{ responseStatusText() }}
                    </span>
                    <span class="duration-badge">{{ responseDuration() }}ms</span>
                  </div>
                }
              </div>

              <div class="response-body-wrapper">
                @if (responseBody()) {
                  <pre class="response-pre"><code class="language-json">{{ responseBody() }}</code></pre>
                } @else if (executing()) {
                  <div class="empty-response">Esperando respuesta de Spring Boot...</div>
                } @else {
                  <div class="empty-response">Hacé clic en "▶ Ejecutar Request" para disparar la petición.</div>
                }
              </div>

              @if (traceId()) {
                <div class="trace-trigger-bar">
                  <button class="btn btn-purple btn-sm w-full" (click)="loadTrace()">
                    🔍 Inspeccionar Traza de Ciclo de Vida (Trace ID: {{ traceId() }})
                  </button>
                </div>
              }
            </div>
          </div>
        </div>

        @if (showTrace() && currentTrace()) {
          <div class="trace-section-wrapper">
            <app-trace-timeline [trace]="currentTrace()!" (openCode)="openCodeFromClass($event)"></app-trace-timeline>
          </div>
        }

        @if (scenario()!.pitfalls && scenario()!.pitfalls.length > 0) {
          <div class="pitfalls-box">
            <h4>⚠️ Trampas Comunes (Pitfalls)</h4>
            <ul>
              @for (pitfall of scenario()!.pitfalls; track pitfall) {
                <li>{{ pitfall }}</li>
              }
            </ul>
          </div>
        }

        @if (scenario()!.interviewQuestions && scenario()!.interviewQuestions.length > 0) {
          <div class="interview-box">
            <h4>💡 Preguntas de Entrevista Técnica</h4>
            <div class="questions-list">
              @for (q of scenario()!.interviewQuestions; track q; let i = $index) {
                <div class="question-card">
                  <div class="question-title">❓ {{ q }}</div>
                </div>
              }
            </div>
          </div>
        }
      </div>

      <app-code-modal #codeModal></app-code-modal>
    } @else {
      <div class="loading-state">
        <div class="spinner"></div>
        <p>Cargando escenario...</p>
      </div>
    }
  `,
  styles: [`
    .detail-container { max-width: 1200px; margin: 0 auto; padding: 1.5rem; }
    .header-actions { margin-bottom: 1.25rem; }
    .scenario-header { margin-bottom: 1.5rem; }
    .scenario-badge { font-size: 0.75rem; font-weight: 700; color: var(--primary); text-transform: uppercase; letter-spacing: 0.05em; }
    .scenario-header h1 { margin: 0.35rem 0 0.5rem 0; font-size: 2rem; color: #f8fafc; }
    .business-story { font-size: 1.05rem; color: #cbd5e1; line-height: 1.5; margin-bottom: 1rem; }
    .concepts-row { display: flex; align-items: center; gap: 0.75rem; flex-wrap: wrap; }
    .concepts-label { font-size: 0.8rem; font-weight: 600; color: #64748b; }
    .tags { display: flex; gap: 0.35rem; flex-wrap: wrap; }

    .source-files-card {
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      border-radius: 8px;
      padding: 1rem 1.25rem;
      margin-bottom: 1.5rem;
    }
    .source-files-card h4 { margin: 0 0 0.75rem 0; font-size: 0.9rem; color: #cbd5e1; }
    .file-buttons { display: flex; gap: 0.5rem; flex-wrap: wrap; }

    .interactive-runner {
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      border-radius: 10px;
      padding: 1.5rem;
      margin-bottom: 1.5rem;
    }
    .runner-header { border-bottom: 1px solid var(--border-color); padding-bottom: 1rem; margin-bottom: 1.25rem; }
    .runner-title h3 { margin: 0; font-size: 1.25rem; color: #f8fafc; }
    .runner-title p { margin: 0.25rem 0 0.75rem 0; font-size: 0.85rem; color: #94a3b8; }
    .step-tabs { display: flex; gap: 0.5rem; flex-wrap: wrap; }
    .step-tab-btn {
      background: var(--bg-main);
      border: 1px solid var(--border-color);
      color: #94a3b8;
      padding: 0.4rem 0.85rem;
      border-radius: 6px;
      font-size: 0.825rem;
      cursor: pointer;
      transition: all 0.15s;
    }
    .step-tab-btn.active {
      background: rgba(56, 189, 248, 0.15);
      border-color: var(--primary);
      color: var(--primary);
      font-weight: 600;
    }

    .runner-body { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; }
    @media (max-width: 900px) { .runner-body { grid-template-columns: 1fr; } }
    
    .panel { background: var(--bg-main); border: 1px solid var(--border-color); border-radius: 8px; padding: 1rem; display: flex; flex-direction: column; }
    .panel-title { font-size: 0.8rem; font-weight: 700; color: #64748b; text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 0.75rem; }

    .http-url-bar { display: flex; gap: 0.5rem; align-items: center; margin-bottom: 0.75rem; }
    .method-badge {
      font-size: 0.75rem; font-weight: 700; padding: 0.4rem 0.6rem; border-radius: 4px; color: #fff; background: #3b82f6; text-transform: uppercase;
    }
    .method-badge.get { background: #0284c7; }
    .method-badge.post { background: #16a34a; }
    .method-badge.put { background: #d97706; }
    .method-badge.patch { background: #9333ea; }
    .method-badge.delete { background: #dc2626; }

    .url-input {
      flex: 1;
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      color: #f1f5f9;
      padding: 0.4rem 0.6rem;
      border-radius: 4px;
      font-family: var(--font-mono);
      font-size: 0.85rem;
    }
    .form-group { margin-bottom: 0.75rem; }
    .form-group label { display: block; font-size: 0.75rem; color: #94a3b8; margin-bottom: 0.25rem; }
    .code-input {
      width: 100%;
      box-sizing: border-box;
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      color: #38bdf8;
      padding: 0.5rem;
      border-radius: 4px;
      font-size: 0.8rem;
      resize: vertical;
    }
    .btn-execute { margin-top: auto; padding: 0.6rem; font-weight: 600; }

    .response-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem; }
    .status-tags { display: flex; gap: 0.5rem; align-items: center; }
    .status-badge { font-size: 0.75rem; font-weight: 700; padding: 0.2rem 0.5rem; border-radius: 4px; }
    .status-2xx { background: #065f46; color: #a7f3d0; }
    .status-4xx { background: #854d0e; color: #fef08a; }
    .status-403, .status-401 { background: #7c2d12; color: #fecaca; }
    .status-5xx { background: #7f1d1d; color: #fecaca; }
    .duration-badge { font-size: 0.75rem; color: #94a3b8; font-family: var(--font-mono); }

    .response-body-wrapper {
      flex: 1;
      min-height: 200px;
      max-height: 350px;
      overflow-y: auto;
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      border-radius: 6px;
      padding: 0.75rem;
    }
    .response-pre { margin: 0; font-family: var(--font-mono); font-size: 0.8rem; color: #f8fafc; }
    .empty-response { color: #64748b; font-size: 0.85rem; text-align: center; padding: 3rem 1rem; }
    .trace-trigger-bar { margin-top: 0.75rem; }
    .btn-purple { background: #7c3aed; color: #fff; border-radius: 6px; }
    .btn-purple:hover { background: #6d28d9; }
    .w-full { width: 100%; }

    .trace-section-wrapper {
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      border-radius: 10px;
      padding: 1.25rem 1.5rem;
      margin-bottom: 1.5rem;
    }

    .pitfalls-box {
      background: rgba(245, 158, 11, 0.08);
      border-left: 4px solid #f59e0b;
      border-radius: 0 8px 8px 0;
      padding: 1rem 1.25rem;
      margin-bottom: 1.5rem;
    }
    .pitfalls-box h4 { margin: 0 0 0.5rem 0; color: #fbbf24; font-size: 0.95rem; }
    .pitfalls-box ul { margin: 0; padding-left: 1.25rem; color: #cbd5e1; font-size: 0.875rem; line-height: 1.5; }

    .interview-box {
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      border-radius: 10px;
      padding: 1.25rem;
      margin-bottom: 2rem;
    }
    .interview-box h4 { margin: 0 0 1rem 0; font-size: 1rem; color: #38bdf8; }
    .questions-list { display: flex; flex-direction: column; gap: 0.5rem; }
    .question-card {
      background: var(--bg-main);
      border: 1px solid var(--border-color);
      border-radius: 6px;
      padding: 0.75rem 1rem;
    }
    .question-title { font-size: 0.875rem; font-weight: 500; color: #f1f5f9; }

    .loading-state { text-align: center; padding: 4rem; color: #94a3b8; }
    .spinner {
      width: 36px; height: 36px; border: 3px solid rgba(56, 189, 248, 0.2);
      border-top-color: var(--primary); border-radius: 50%;
      animation: spin 1s linear infinite; margin: 0 auto 1rem;
    }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class ScenarioDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private explorerService = inject(ExplorerService);
  private authService = inject(AuthService);
  
  @ViewChild('codeModal') codeModal!: CodeModalComponent;

  scenario = signal<ScenarioInfo | null>(null);
  selectedStepIndex = signal<number>(0);
  currentStep = signal<ScenarioStep | null>(null);

  requestMethod = signal<string>('GET');
  requestUrl = '';
  requestHeadersStr = '{}';
  requestBodyStr = '{}';

  executing = signal<boolean>(false);
  responseStatus = signal<number | null>(null);
  responseStatusText = signal<string>('');
  responseBody = signal<string>('');
  responseDuration = signal<number>(0);
  traceId = signal<string | null>(null);

  showTrace = signal<boolean>(false);
  currentTrace = signal<TraceData | null>(null);

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.loadScenario(id);
      }
    });
  }

  loadScenario(id: string) {
    this.explorerService.getScenario(id).subscribe(data => {
      if (data) {
        this.scenario.set(data);
        this.selectStep(0);
      }
    });
  }

  selectStep(index: number) {
    this.selectedStepIndex.set(index);
    const steps = this.scenario()?.steps;
    if (steps && steps[index]) {
      const step = steps[index];
      this.currentStep.set(step);
      this.requestMethod.set(step.request.method || 'GET');
      this.requestUrl = step.request.url || '';
      
      // Inject current active token if header placeholders are present
      const headers = { ...step.request.headers };
      if (this.authService.token()) {
        for (const k in headers) {
          if (headers[k].includes('{{admin_token}}') || headers[k].includes('{{user1_token}}') || headers[k].includes('{{user2_token}}')) {
            headers[k] = `Bearer ${this.authService.token()}`;
          }
        }
      }
      this.requestHeadersStr = JSON.stringify(headers, null, 2);
      this.requestBodyStr = typeof step.request.body === 'string' 
        ? step.request.body 
        : JSON.stringify(step.request.body || {}, null, 2);
      
      this.responseStatus.set(null);
      this.responseBody.set('');
      this.traceId.set(null);
      this.showTrace.set(false);
      this.currentTrace.set(null);
    }
  }

  executeRequest() {
    this.executing.set(true);
    let parsedHeaders: Record<string, string> = {};
    try {
      parsedHeaders = JSON.parse(this.requestHeadersStr);
    } catch(e) {}

    let parsedBody: any = null;
    if (this.requestMethod() !== 'GET' && this.requestMethod() !== 'DELETE' && this.requestBodyStr) {
      try {
        parsedBody = JSON.parse(this.requestBodyStr);
      } catch(e) {
        parsedBody = this.requestBodyStr;
      }
    }

    this.explorerService.executeCustomRequest(this.requestMethod(), this.requestUrl, parsedHeaders, parsedBody).subscribe({
      next: (res) => {
        this.executing.set(false);
        this.responseStatus.set(res.status);
        this.responseStatusText.set(res.statusText);
        this.responseDuration.set(res.durationMs);
        this.traceId.set(res.traceId || null);
        
        if (typeof res.body === 'object') {
          this.responseBody.set(JSON.stringify(res.body, null, 2));
        } else {
          this.responseBody.set(String(res.body || ''));
        }
      },
      error: (err) => {
        this.executing.set(false);
        this.responseStatus.set(err.status || 500);
        this.responseStatusText.set(err.statusText || 'Error');
        this.responseBody.set(JSON.stringify(err.error || err.message, null, 2));
      }
    });
  }

  loadTrace() {
    const tid = this.traceId();
    if (!tid) return;
    this.explorerService.getTrace(tid).subscribe({
      next: (data) => {
        this.currentTrace.set(data);
        this.showTrace.set(true);
      },
      error: () => {
        alert('No se encontró la traza para ID: ' + tid);
      }
    });
  }

  openCode(file: string, startLine?: number, endLine?: number) {
    this.codeModal.open(file, startLine, endLine);
  }

  openCodeFromClass(className: string) {
    // Convert class name to file path (e.g., ProductController -> ProductController.java)
    let file = className;
    if (!file.endsWith('.java')) {
      file += '.java';
    }
    this.codeModal.open(file);
  }

  getStatusClass(status: number): string {
    if (status >= 200 && status < 300) return 'status-2xx';
    if (status === 401 || status === 403) return 'status-403';
    if (status >= 400 && status < 500) return 'status-404';
    return 'status-5xx';
  }
}
