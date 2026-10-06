import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../core/services/auth.service';
import { ExplorerService } from '../core/services/explorer.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  template: `
    <header class="header">
      <div class="brand">
        <div class="logo-badge">☕ Spring Boot</div>
        <h2>Java/Spring Explorer</h2>
        <span class="badge badge-success">Angular 19</span>
      </div>
      
      <div class="actions">
        <a href="http://localhost:8080/swagger-ui.html" target="_blank" rel="noopener noreferrer" class="btn btn-outline btn-sm">
          📖 Swagger UI
        </a>
        <a href="http://localhost:8080/actuator/health" target="_blank" rel="noopener noreferrer" class="btn btn-outline btn-sm">
          🩺 Actuator
        </a>
        
        <button class="btn btn-danger btn-sm" (click)="resetData()" [disabled]="resetting()">
          {{ resetting() ? '⏳ Reiniciando...' : '🔄 Reiniciar Semilla' }}
        </button>

        <div class="user-switcher">
          <span class="current-user">
            @if (auth.isAuthenticated()) {
              Sesión: <strong class="user-name">{{ auth.currentUser() }}</strong> 
              <span class="role-pill" [class.admin]="auth.isAdmin()">{{ auth.role() }}</span>
            } @else {
              <span class="text-muted">Sin Token (Anónimo)</span>
            }
          </span>
          <select (change)="onUserChange($event)">
            <option value="admin" [selected]="auth.currentUser() === 'admin'">ADMIN (admin/admin123)</option>
            <option value="user1" [selected]="auth.currentUser() === 'user1'">USER 1 (Acme Corp - Premium)</option>
            <option value="user2" [selected]="auth.currentUser() === 'user2'">USER 2 (Globex Inc - Regular)</option>
            <option value="anon" [selected]="!auth.isAuthenticated()">Sin Token (401)</option>
          </select>
        </div>
      </div>
    </header>
  `,
  styles: [`
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem 1.5rem;
      background: var(--bg-card);
      border-bottom: 1px solid var(--border-color);
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    }
    .brand { display: flex; align-items: center; gap: 0.75rem; }
    .logo-badge {
      background: #047857;
      color: #fff;
      font-size: 0.75rem;
      font-weight: 700;
      padding: 0.2rem 0.5rem;
      border-radius: 4px;
    }
    .brand h2 { margin: 0; font-size: 1.15rem; font-weight: 700; color: #f8fafc; }
    .actions { display: flex; align-items: center; gap: 0.75rem; }
    .btn-sm { padding: 0.35rem 0.65rem; font-size: 0.8rem; border-radius: 6px; cursor: pointer; }
    .user-switcher { 
      display: flex; 
      align-items: center; 
      gap: 0.5rem; 
      background: var(--bg-main); 
      padding: 0.35rem 0.75rem; 
      border-radius: 6px; 
      border: 1px solid var(--border-color); 
    }
    .current-user { font-size: 0.825rem; display: flex; align-items: center; gap: 0.35rem; }
    .user-name { color: var(--primary); }
    .role-pill {
      font-size: 0.7rem;
      padding: 0.1rem 0.4rem;
      border-radius: 4px;
      background: #334155;
      color: #94a3b8;
    }
    .role-pill.admin { background: #b45309; color: #fef3c7; }
    select { 
      background: var(--bg-card); 
      border: 1px solid var(--border-color); 
      color: var(--text-primary); 
      font-size: 0.8rem; 
      padding: 0.25rem 0.5rem;
      border-radius: 4px;
      cursor: pointer; 
      outline: none; 
    }
    .text-muted { color: #64748b; }
  `]
})
export class HeaderComponent {
  auth = inject(AuthService);
  explorerService = inject(ExplorerService);
  
  resetting = signal(false);

  onUserChange(event: Event) {
    const target = event.target as HTMLSelectElement;
    const val = target.value as 'admin' | 'user1' | 'user2' | 'anon';
    this.auth.quickLogin(val);
  }

  resetData() {
    this.resetting.set(true);
    this.explorerService.resetData().subscribe({
      next: () => {
        this.resetting.set(false);
        alert('✅ Datos semilla reiniciados exitosamente en la memoria del backend.');
      },
      error: () => {
        this.resetting.set(false);
        alert('⚠️ No se pudo reiniciar (¿El backend está ejecutándose en localhost:8080?)');
      }
    });
  }
}
