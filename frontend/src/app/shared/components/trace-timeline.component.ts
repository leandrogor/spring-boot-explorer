import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TraceData, TraceStep } from '../../core/models/models';

@Component({
  selector: 'app-trace-timeline',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="timeline-container">
      @if (trace && trace.steps && trace.steps.length > 0) {
        <div class="trace-header">
          <h4>Traza de Ciclo de Vida: <code>{{ trace.traceId }}</code></h4>
          <span class="badge badge-primary">{{ getTotalDuration() }}ms</span>
        </div>
        <div class="timeline">
          @for (step of trace.steps; track $index; let i = $index) {
            <div class="timeline-step">
              <div class="step-indicator">
                <div class="circle"></div>
                @if (i < trace.steps.length - 1) {
                  <div class="line"></div>
                }
              </div>
              <div class="step-content">
                <div class="step-header">
                  <span class="badge" [ngClass]="getBadgeClass(step.component)">{{ step.component }}</span>
                  <span class="step-class">{{ step.className }}</span>
                  <span class="step-time">+{{ step.durationMs }}ms</span>
                </div>
                
                <div class="step-detail">
                  {{ step.detail }}
                </div>

                @if (step.className) {
                  <div class="step-actions">
                    <button class="btn btn-outline btn-xs" (click)="onOpenCode(step.className)">
                      📄 Ver Código de {{ step.className }}
                    </button>
                  </div>
                }
              </div>
            </div>
          }
        </div>
      } @else {
        <div class="empty-state">
          No hay datos de traza disponibles para este request o aún no ha sido ejecutado.
        </div>
      }
    </div>
  `,
  styles: [`
    .timeline-container { padding: 0.5rem 0; }
    .trace-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .trace-header h4 { margin: 0; font-size: 0.95rem; color: #cbd5e1; }
    .trace-header code { color: var(--primary); background: rgba(56, 189, 248, 0.1); padding: 0.2rem 0.4rem; border-radius: 4px; }
    
    .timeline { display: flex; flex-direction: column; }
    .timeline-step { display: flex; gap: 1rem; position: relative; }
    
    .step-indicator { display: flex; flex-direction: column; align-items: center; width: 16px; flex-shrink: 0; }
    .circle { width: 12px; height: 12px; border-radius: 50%; background: var(--primary); margin-top: 6px; z-index: 2; box-shadow: 0 0 8px var(--primary); }
    .line { flex: 1; width: 2px; background: #334155; margin-top: 4px; margin-bottom: -4px; }
    
    .step-content {
      flex: 1;
      margin-bottom: 1rem;
      padding: 0.75rem 1rem;
      background: var(--bg-main);
      border: 1px solid var(--border-color);
      border-radius: 8px;
    }
    .step-header { display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.35rem; }
    .step-class { font-weight: 600; font-size: 0.875rem; color: #f1f5f9; }
    .step-time { margin-left: auto; font-size: 0.75rem; color: #94a3b8; font-family: var(--font-mono); }
    
    .step-detail { font-size: 0.825rem; color: #94a3b8; line-height: 1.4; margin-bottom: 0.5rem; }
    .step-actions { display: flex; }
    .btn-xs { padding: 0.15rem 0.5rem; font-size: 0.75rem; border-radius: 4px; }
    
    .empty-state { padding: 2rem; text-align: center; color: #64748b; font-size: 0.875rem; }
  `]
})
export class TraceTimelineComponent {
  @Input() trace?: TraceData;
  @Output() openCode = new EventEmitter<string>();

  getTotalDuration(): number {
    if (!this.trace || !this.trace.steps || this.trace.steps.length === 0) return 0;
    const lastStep = this.trace.steps[this.trace.steps.length - 1];
    return lastStep.durationMs || 0;
  }

  getBadgeClass(component: string): string {
    switch (component?.toLowerCase()) {
      case 'filter': return 'badge-warning';
      case 'security': return 'badge-purple';
      case 'controller': return 'badge-primary';
      case 'service': return 'badge-success';
      case 'repository': return 'badge-secondary';
      case 'exception': return 'badge-danger';
      default: return 'badge-primary';
    }
  }

  onOpenCode(className: string) {
    this.openCode.emit(className);
  }
}
