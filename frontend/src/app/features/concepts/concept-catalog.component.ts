import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ExplorerService } from '../../core/services/explorer.service';
import { ConceptInfo, BridgeInfo } from '../../core/models/models';
import { BridgeCardComponent } from '../../shared/components/bridge-card.component';

@Component({
  selector: 'app-concept-catalog',
  standalone: true,
  imports: [CommonModule, RouterModule, BridgeCardComponent],
  template: `
    <div class="concepts-page">
      <div class="page-header">
        <h1>💡 Catálogo de Conceptos Spring Boot & Java</h1>
        <p class="subtitle">
          Glosario técnico comparativo diseñado para desarrolladores con background en TypeScript/Express y C#/ASP.NET Core.
        </p>
        
        <div class="category-filters">
          @for (cat of categories; track cat) {
            <button class="filter-btn" 
                    [class.active]="selectedCategory() === cat"
                    (click)="selectedCategory.set(cat)">
              {{ cat }}
            </button>
          }
        </div>
      </div>

      <div class="concepts-list">
        @for (concept of filteredConcepts(); track concept.id) {
          <div class="concept-card">
            <div class="card-header">
              <div class="title-wrap">
                <h2>{{ concept.name }}</h2>
                <span class="badge badge-primary">{{ concept.category }}</span>
              </div>
            </div>
            
            <p class="concept-description">{{ concept.description }}</p>
            
            <app-bridge-card [bridge]="toBridgeInfo(concept)"></app-bridge-card>
            
            @if (concept.scenarioIds && concept.scenarioIds.length > 0) {
              <div class="scenarios-link-row">
                <span class="link-label">Presente en Escenarios:</span>
                <div class="scenario-links">
                  @for (scId of concept.scenarioIds; track scId) {
                    <a [routerLink]="['/scenarios', scId]" class="btn btn-outline btn-xs">
                      🎯 {{ scId }} →
                    </a>
                  }
                </div>
              </div>
            }
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    .concepts-page { max-width: 1100px; margin: 0 auto; padding: 1.5rem; }
    .page-header { margin-bottom: 2rem; }
    .page-header h1 { margin: 0 0 0.5rem 0; font-size: 1.75rem; color: #f8fafc; }
    .subtitle { margin: 0 0 1.25rem 0; color: #94a3b8; font-size: 0.95rem; }
    .category-filters { display: flex; gap: 0.5rem; flex-wrap: wrap; }
    .filter-btn {
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      color: #94a3b8;
      padding: 0.4rem 0.85rem;
      border-radius: 6px;
      font-size: 0.825rem;
      cursor: pointer;
      transition: all 0.15s;
    }
    .filter-btn.active {
      background: rgba(56, 189, 248, 0.15);
      border-color: var(--primary);
      color: var(--primary);
      font-weight: 600;
    }
    .concepts-list { display: flex; flex-direction: column; gap: 1.5rem; }
    .concept-card {
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      border-radius: 10px;
      padding: 1.5rem;
    }
    .card-header { margin-bottom: 0.75rem; }
    .title-wrap { display: flex; justify-content: space-between; align-items: center; }
    .title-wrap h2 { margin: 0; font-size: 1.25rem; color: #f1f5f9; }
    .concept-description { font-size: 0.95rem; color: #cbd5e1; line-height: 1.5; margin-bottom: 1.25rem; }
    .scenarios-link-row { display: flex; align-items: center; gap: 0.75rem; margin-top: 1rem; flex-wrap: wrap; }
    .link-label { font-size: 0.8rem; font-weight: 600; color: #64748b; }
    .scenario-links { display: flex; gap: 0.5rem; flex-wrap: wrap; }
    .btn-xs { padding: 0.25rem 0.6rem; font-size: 0.75rem; border-radius: 4px; }
  `]
})
export class ConceptCatalogComponent implements OnInit {
  private explorerService = inject(ExplorerService);
  
  concepts = signal<ConceptInfo[]>([]);
  categories = ['Todos', 'Web', 'Core', 'Security', 'Java', 'Config', 'Operations', 'Testing'];
  selectedCategory = signal('Todos');

  ngOnInit() {
    this.explorerService.getConcepts().subscribe(data => {
      this.concepts.set(data);
    });
  }

  filteredConcepts() {
    const cat = this.selectedCategory();
    if (cat === 'Todos') return this.concepts();
    return this.concepts().filter(c => c.category.toLowerCase() === cat.toLowerCase());
  }

  toBridgeInfo(concept: ConceptInfo): BridgeInfo {
    return {
      express: concept.bridgeExpress,
      aspnet: concept.bridgeAspNet
    };
  }
}
