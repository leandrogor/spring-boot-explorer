import { Component, inject, signal, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExplorerService } from '../../core/services/explorer.service';
import { ArchitectureLayer } from '../../core/models/models';
import { CodeModalComponent } from '../../shared/components/code-modal.component';

@Component({
  selector: 'app-architecture-map',
  standalone: true,
  imports: [CommonModule, CodeModalComponent],
  template: `
    <div class="arch-page">
      <div class="page-header">
        <h1>🏛️ Mapa de Arquitectura y Beans de Spring</h1>
        <p class="subtitle">
          Recorrido vertical de capas por las que transita una request HTTP, comparando cada capa y bean con sus equivalentes en Express (TS) y ASP.NET Core (C#).
        </p>
      </div>

      <div class="layers-list">
        @for (layer of layers(); track layer.id; let i = $index) {
          <div class="layer-card">
            <div class="layer-header">
              <div class="layer-title-wrap">
                <span class="layer-number">Capa {{ i + 1 }}</span>
                <h2>{{ layer.name }}</h2>
              </div>
            </div>
            
            <p class="layer-desc">{{ layer.description }}</p>
            
            <div class="layer-bridges">
              <div class="bridge-badge ts">
                <span class="bridge-tag">Express/TS:</span>
                <span>{{ layer.expressEquivalent }}</span>
              </div>
              <div class="bridge-badge cs">
                <span class="bridge-tag">ASP.NET:</span>
                <span>{{ layer.aspnetEquivalent }}</span>
              </div>
            </div>

            <div class="beans-section">
              <div class="beans-header">Beans de Spring en esta Capa:</div>
              <div class="beans-grid">
                @for (comp of layer.components; track comp.name) {
                  <div class="bean-item">
                    <div class="bean-info">
                      <div class="bean-name-row">
                        <span class="bean-name">{{ comp.name }}</span>
                        <span class="bean-annotation">{{ comp.annotation }}</span>
                      </div>
                      <span class="bean-role">{{ comp.role }}</span>
                    </div>
                    <button class="btn btn-outline btn-xs" (click)="openCode(comp.sourceFile)">
                      📄 Ver Código
                    </button>
                  </div>
                }
              </div>
            </div>
          </div>
        }
      </div>

      <app-code-modal #codeModal></app-code-modal>
    </div>
  `,
  styles: [`
    .arch-page { max-width: 1100px; margin: 0 auto; padding: 1.5rem; }
    .page-header { margin-bottom: 2rem; }
    .page-header h1 { margin: 0 0 0.5rem 0; font-size: 1.75rem; color: #f8fafc; }
    .subtitle { margin: 0; color: #94a3b8; font-size: 0.95rem; line-height: 1.5; }

    .layers-list { display: flex; flex-direction: column; gap: 1.5rem; }
    .layer-card {
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      border-left: 4px solid var(--primary);
      border-radius: 10px;
      padding: 1.5rem;
    }
    .layer-title-wrap { display: flex; flex-direction: column; gap: 0.25rem; }
    .layer-number { font-size: 0.75rem; font-weight: 700; color: var(--primary); text-transform: uppercase; letter-spacing: 0.05em; }
    .layer-title-wrap h2 { margin: 0; font-size: 1.25rem; color: #f1f5f9; }
    
    .layer-desc { font-size: 0.9rem; color: #cbd5e1; line-height: 1.5; margin: 0.75rem 0 1rem 0; }
    
    .layer-bridges { display: flex; gap: 0.75rem; flex-wrap: wrap; margin-bottom: 1.25rem; }
    .bridge-badge {
      display: flex;
      gap: 0.4rem;
      align-items: center;
      background: var(--bg-main);
      border: 1px solid var(--border-color);
      padding: 0.4rem 0.75rem;
      border-radius: 6px;
      font-size: 0.8rem;
    }
    .bridge-badge.ts .bridge-tag { color: #38bdf8; font-weight: 700; }
    .bridge-badge.cs .bridge-tag { color: #a855f7; font-weight: 700; }

    .beans-section { background: var(--bg-main); border: 1px solid var(--border-color); border-radius: 8px; padding: 1rem; }
    .beans-header { font-size: 0.75rem; font-weight: 700; color: #64748b; text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 0.75rem; }
    .beans-grid { display: grid; grid-template-columns: 1fr; gap: 0.5rem; }

    .bean-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.6rem 0.85rem;
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      border-radius: 6px;
    }
    .bean-info { display: flex; flex-direction: column; gap: 0.2rem; }
    .bean-name-row { display: flex; align-items: center; gap: 0.5rem; }
    .bean-name { font-weight: 600; font-size: 0.85rem; color: #f1f5f9; }
    .bean-annotation { font-family: var(--font-mono); font-size: 0.75rem; color: var(--primary); background: rgba(56, 189, 248, 0.1); padding: 0.1rem 0.35rem; border-radius: 4px; }
    .bean-role { font-size: 0.75rem; color: #94a3b8; }
    .btn-xs { padding: 0.25rem 0.6rem; font-size: 0.75rem; border-radius: 4px; }
  `]
})
export class ArchitectureMapComponent implements OnInit {
  private explorerService = inject(ExplorerService);
  
  @ViewChild('codeModal') codeModal!: CodeModalComponent;

  layers = signal<ArchitectureLayer[]>([]);

  ngOnInit() {
    this.explorerService.getArchitecture().subscribe(data => {
      this.layers.set(data || []);
    });
  }

  openCode(path: string) {
    if (path) {
      this.codeModal.open(path);
    }
  }
}
