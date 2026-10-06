import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <aside class="sidebar">
      <div class="nav-section-title">EXPLORADOR</div>
      <nav class="nav-links">
        <a routerLink="/scenarios" routerLinkActive="active" class="nav-item">
          <span class="nav-icon">🎯</span>
          <span class="nav-text">8 Escenarios Guiados</span>
        </a>
        
        <a routerLink="/concepts" routerLinkActive="active" class="nav-item">
          <span class="nav-icon">💡</span>
          <span class="nav-text">Catálogo de Conceptos</span>
        </a>
        
        <a routerLink="/state" routerLinkActive="active" class="nav-item">
          <span class="nav-icon">📊</span>
          <span class="nav-text">Estado en Memoria</span>
        </a>
        
        <a routerLink="/architecture" routerLinkActive="active" class="nav-item">
          <span class="nav-icon">🏛️</span>
          <span class="nav-text">Mapa de Arquitectura</span>
        </a>
      </nav>

      <div class="nav-section-title mt-4">HERRAMIENTAS SPRING</div>
      <nav class="nav-links">
        <a href="http://localhost:8080/swagger-ui.html" target="_blank" rel="noopener noreferrer" class="nav-item external">
          <span class="nav-icon">📑</span>
          <span class="nav-text">Swagger OpenAPI ↗</span>
        </a>
        
        <a href="http://localhost:8080/actuator/health" target="_blank" rel="noopener noreferrer" class="nav-item external">
          <span class="nav-icon">🩺</span>
          <span class="nav-text">Actuator Health ↗</span>
        </a>
        
        <a href="http://localhost:8080/actuator/metrics" target="_blank" rel="noopener noreferrer" class="nav-item external">
          <span class="nav-icon">📈</span>
          <span class="nav-text">Actuator Metrics ↗</span>
        </a>
      </nav>

      <div class="sidebar-footer">
        <div class="footer-card">
          <div class="footer-title">⚡ Stack Activo</div>
          <div class="footer-desc">Java 21 • Spring Boot 3.3 • ConcurrentHashMap Mock • JWT</div>
        </div>
      </div>
    </aside>
  `,
  styles: [`
    .sidebar {
      width: 250px;
      background: var(--bg-card);
      border-right: 1px solid var(--border-color);
      display: flex;
      flex-direction: column;
      height: 100%;
      padding: 1.25rem 0.75rem;
      flex-shrink: 0;
    }
    .nav-section-title {
      font-size: 0.7rem;
      font-weight: 700;
      color: #64748b;
      letter-spacing: 0.05em;
      padding: 0.5rem 0.75rem 0.25rem;
      text-transform: uppercase;
    }
    .mt-4 { margin-top: 1.25rem; }
    .nav-links {
      display: flex;
      flex-direction: column;
      gap: 0.35rem;
    }
    .nav-item {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      padding: 0.65rem 0.75rem;
      color: var(--text-secondary);
      text-decoration: none;
      border-radius: 6px;
      font-size: 0.875rem;
      font-weight: 500;
      transition: all 0.15s ease;
    }
    .nav-item:hover {
      background: rgba(56, 189, 248, 0.1);
      color: var(--primary);
    }
    .nav-item.active {
      background: rgba(56, 189, 248, 0.15);
      color: var(--primary);
      border-left: 3px solid var(--primary);
    }
    .nav-icon { font-size: 1.1rem; }
    .external { color: #94a3b8; font-size: 0.825rem; }
    .sidebar-footer {
      margin-top: auto;
      padding-top: 1rem;
    }
    .footer-card {
      background: var(--bg-main);
      border: 1px solid var(--border-color);
      border-radius: 6px;
      padding: 0.75rem;
    }
    .footer-title {
      font-size: 0.75rem;
      font-weight: 700;
      color: var(--primary);
      margin-bottom: 0.25rem;
    }
    .footer-desc {
      font-size: 0.7rem;
      color: #94a3b8;
      line-height: 1.3;
    }
  `]
})
export class SidebarComponent {}
