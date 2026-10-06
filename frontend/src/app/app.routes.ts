import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'scenarios', pathMatch: 'full' },
  { 
    path: 'scenarios', 
    loadComponent: () => import('./features/scenarios/scenario-list.component').then(m => m.ScenarioListComponent) 
  },
  { 
    path: 'scenarios/:id', 
    loadComponent: () => import('./features/scenarios/scenario-detail.component').then(m => m.ScenarioDetailComponent) 
  },
  { 
    path: 'concepts', 
    loadComponent: () => import('./features/concepts/concept-catalog.component').then(m => m.ConceptCatalogComponent) 
  },
  { 
    path: 'state', 
    loadComponent: () => import('./features/state/business-state.component').then(m => m.BusinessStateComponent) 
  },
  { 
    path: 'architecture', 
    loadComponent: () => import('./features/architecture/architecture-map.component').then(m => m.ArchitectureMapComponent) 
  },
  { path: '**', redirectTo: 'scenarios' }
];
