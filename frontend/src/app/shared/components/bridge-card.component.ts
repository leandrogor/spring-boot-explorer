import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BridgeInfo } from '../../core/models/models';

@Component({
  selector: 'app-bridge-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (bridge) {
      <div class="bridge-container card">
        <h4 class="bridge-title">🌉 El Puente (Equivalencias)</h4>
        <div class="bridge-grid">
          <div class="bridge-col">
            <div class="tech-header">
              <span class="badge badge-warning">Express / TS</span>
            </div>
            <p>{{ bridge.express }}</p>
          </div>
          <div class="divider"></div>
          <div class="bridge-col">
            <div class="tech-header">
              <span class="badge badge-purple">C# / ASP.NET Core</span>
            </div>
            <p>{{ bridge.aspnet }}</p>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .bridge-container { margin-bottom: 1.5rem; background: linear-gradient(to right, rgba(245,158,11,0.05), rgba(168,85,247,0.05)); border: 1px solid var(--border-color); }
    .bridge-title { margin-top: 0; margin-bottom: 1rem; font-size: 1.125rem; color: var(--text-primary); }
    .bridge-grid { display: flex; gap: 1rem; }
    .bridge-col { flex: 1; }
    .tech-header { margin-bottom: 0.5rem; }
    .bridge-col p { margin: 0; font-size: 0.875rem; color: var(--text-secondary); line-height: 1.6; }
    .divider { width: 1px; background: var(--border-color); }
  `]
})
export class BridgeCardComponent {
  @Input() bridge?: BridgeInfo;
}
