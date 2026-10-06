import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ExplorerService } from '../../core/services/explorer.service';
import { ScenarioInfo } from '../../core/models/models';

@Component({
  selector: 'app-scenario-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="scenarios-container">
      <div class="page-header">
        <div>
          <h1>🎯 Escenarios Guiados de Negocio</h1>
          <p class="subtitle">
            Cada escenario ilustra un concepto técnico de Java 21 y Spring Boot 3 anclado a un caso real de pedidos e inventario.
          </p>
        </div>
      </div>

      @if (loading()) {
        <div class="loading-state">
          <div class="spinner"></div>
          <p>Cargando escenarios del backend...</p>
        </div>
      } @else {
        <div class="scenarios-grid">
          @for (scenario of scenarios(); track scenario.id; let i = $index) {
            <div class="scenario-card">
              <div class="card-header">
                <span class="scenario-number">Escenario {{ i + 1 }}</span>
                <h3 class="card-title">{{ scenario.title }}</h3>
              </div>
              
              <p class="card-story">{{ scenario.story }}</p>

              <div class="concepts-section">
                <div class="section-label">Conceptos Spring/Java:</div>
                <div class="concepts-tags">
                  @for (concept of scenario.concepts; track concept) {
                    <span class="badge badge-primary">{{ concept }}</span>
                  }
                </div>
              </div>

              <div class="bridge-preview">
                <div class="bridge-row">
                  <span class="bridge-tech ts">TS/Express:</span>
                  <span class="bridge-text">{{ scenario.bridge.express }}</span>
                </div>
                <div class="bridge-row">
                  <span class="bridge-tech cs">C#/.NET:</span>
                  <span class="bridge-text">{{ scenario.bridge.aspnet }}</span>
                </div>
              </div>

              <div class="card-footer">
                <div class="step-count">
                  {{ scenario.steps.length }} {{ scenario.steps.length === 1 ? 'paso ejecutable' : 'pasos ejecutables' }}
                </div>
                <a [routerLink]="['/scenarios', scenario.id]" class="btn btn-primary btn-sm">
                  Explorar y Probar →
                </a>
              </div>
            </div>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    .scenarios-container { padding: 1.5rem; max-width: 1300px; margin: 0 auto; }
    .page-header { margin-bottom: 2rem; }
    .page-header h1 { margin: 0 0 0.5rem 0; font-size: 1.75rem; color: #f8fafc; }
    .subtitle { margin: 0; color: #94a3b8; font-size: 0.95rem; }
    .loading-state { text-align: center; padding: 4rem 0; color: #94a3b8; }
    .spinner {
      width: 36px; height: 36px; border: 3px solid rgba(56, 189, 248, 0.2);
      border-top-color: var(--primary); border-radius: 50%;
      animation: spin 1s linear infinite; margin: 0 auto 1rem;
    }
    @keyframes spin { to { transform: rotate(360deg); } }
    .scenarios-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
      gap: 1.5rem;
    }
    .scenario-card {
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      border-radius: 10px;
      padding: 1.5rem;
      display: flex;
      flex-direction: column;
      transition: transform 0.2s ease, border-color 0.2s ease;
    }
    .scenario-card:hover {
      transform: translateY(-2px);
      border-color: rgba(56, 189, 248, 0.4);
    }
    .card-header { margin-bottom: 0.75rem; }
    .scenario-number {
      font-size: 0.75rem;
      font-weight: 700;
      color: var(--primary);
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }
    .card-title {
      margin: 0.25rem 0 0 0;
      font-size: 1.15rem;
      color: #f1f5f9;
      font-weight: 600;
    }
    .card-story {
      color: #94a3b8;
      font-size: 0.875rem;
      line-height: 1.45;
      margin-bottom: 1rem;
    }
    .concepts-section { margin-bottom: 1rem; }
    .section-label { font-size: 0.75rem; color: #64748b; font-weight: 600; margin-bottom: 0.35rem; }
    .concepts-tags { display: flex; flex-wrap: wrap; gap: 0.35rem; }
    .bridge-preview {
      background: var(--bg-main);
      border: 1px solid var(--border-color);
      border-radius: 6px;
      padding: 0.65rem 0.75rem;
      margin-bottom: 1.25rem;
      font-size: 0.8rem;
    }
    .bridge-row { display: flex; gap: 0.5rem; margin-bottom: 0.25rem; align-items: baseline; }
    .bridge-row:last-child { margin-bottom: 0; }
    .bridge-tech { font-weight: 700; width: 80px; flex-shrink: 0; }
    .bridge-tech.ts { color: #38bdf8; }
    .bridge-tech.cs { color: #a855f7; }
    .bridge-text { color: #cbd5e1; }
    .card-footer {
      margin-top: auto;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 0.75rem;
      border-top: 1px solid var(--border-color);
    }
    .step-count { font-size: 0.8rem; color: #64748b; }
    .btn-sm { padding: 0.4rem 0.85rem; font-size: 0.825rem; }
  `]
})
export class ScenarioListComponent implements OnInit {
  private explorerService = inject(ExplorerService);
  scenarios = signal<ScenarioInfo[]>([]);
  loading = signal<boolean>(true);

  ngOnInit() {
    this.explorerService.getScenarios().subscribe({
      next: (data) => {
        this.scenarios.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }
}
