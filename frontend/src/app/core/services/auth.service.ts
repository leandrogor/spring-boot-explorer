import { Injectable, computed, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginResponse } from '../models/models';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);

  private _currentUser = signal<string | null>(null);
  private _token = signal<string | null>(null);
  private _role = signal<string | null>(null);

  currentUser = this._currentUser.asReadonly();
  token = this._token.asReadonly();
  role = this._role.asReadonly();

  isAdmin = computed(() => this._role() === 'ADMIN');
  isUser = computed(() => this._role() === 'USER');
  isAuthenticated = computed(() => this._token() !== null);

  constructor() {
    this.quickLogin('admin');
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/auth/login', { username, password }).pipe(
      tap(res => {
        this._currentUser.set(res.username);
        this._token.set(res.token);
        this._role.set(res.role);
      })
    );
  }

  quickLogin(user: 'admin' | 'user1' | 'user2' | 'anon') {
    if (user === 'anon') {
      this.logout();
      return;
    }

    const passwords: Record<string, string> = {
      admin: 'admin123',
      user1: 'user123',
      user2: 'user123'
    };

    this.login(user, passwords[user] || 'user123').subscribe({
      error: (err) => {
        console.warn('Backend not yet running or login failed, falling back to local signal state:', err);
        // Fallback state if backend is offline during frontend build
        this._currentUser.set(user);
        this._token.set(`offline-token-${user}`);
        this._role.set(user === 'admin' ? 'ADMIN' : 'USER');
      }
    });
  }

  logout() {
    this._currentUser.set(null);
    this._token.set(null);
    this._role.set(null);
  }
}
