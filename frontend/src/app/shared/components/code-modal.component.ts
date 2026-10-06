import { Component, Input, Output, EventEmitter, signal, effect, ElementRef, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExplorerService } from '../../core/services/explorer.service';
import * as Prism from 'prismjs';
import 'prismjs/components/prism-java';
import 'prismjs/components/prism-json';

@Component({
  selector: 'app-code-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (isOpen()) {
      <div class="modal-overlay" (click)="close()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <div>
              <h3 class="modal-title">{{ filePath() }}</h3>
              @if (startLine() && endLine()) {
                <span class="modal-subtitle">Lines {{ startLine() }} - {{ endLine() }}</span>
              } @else {
                <span class="modal-subtitle">Total lines: {{ totalLines() }}</span>
              }
            </div>
            <div class="modal-actions">
              <button class="btn btn-outline btn-sm" (click)="copyCode()">Copiar</button>
              <button class="btn btn-danger btn-sm" (click)="close()">Cerrar</button>
            </div>
          </div>
          
          <div class="modal-body">
            @if (loading()) {
              <div class="loading">Cargando código...</div>
            } @else {
              <pre><code #codeElement [class]="'language-' + language()">{{ codeContent() }}</code></pre>
            }
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .modal-overlay {
      position: fixed; top: 0; left: 0; right: 0; bottom: 0;
      background: rgba(15, 23, 42, 0.8);
      backdrop-filter: blur(4px);
      z-index: 1000;
      display: flex; align-items: center; justify-content: center;
      padding: 2rem;
    }
    .modal-content {
      background: var(--bg-card); border: 1px solid var(--border-color);
      border-radius: 0.75rem; width: 100%; max-width: 1000px;
      max-height: 90vh; display: flex; flex-direction: column;
      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
    }
    .modal-header {
      padding: 1rem 1.5rem; border-bottom: 1px solid var(--border-color);
      display: flex; justify-content: space-between; align-items: center;
    }
    .modal-title { margin: 0; font-size: 1.125rem; font-weight: 600; }
    .modal-subtitle { font-size: 0.875rem; color: var(--text-secondary); }
    .modal-actions { display: flex; gap: 0.5rem; }
    .modal-body {
      padding: 1rem; overflow-y: auto; flex: 1;
    }
    .loading { padding: 2rem; text-align: center; color: var(--text-secondary); }
    .btn-sm { padding: 0.25rem 0.75rem; font-size: 0.875rem; }
    pre { margin: 0 !important; border: none !important; border-radius: 0.375rem !important; }
  `]
})
export class CodeModalComponent {
  private explorerService = inject(ExplorerService);

  isOpen = signal(false);
  filePath = signal('');
  startLine = signal<number | undefined>(undefined);
  endLine = signal<number | undefined>(undefined);
  
  codeContent = signal('');
  totalLines = signal(0);
  loading = signal(false);
  language = signal('java');

  @ViewChild('codeElement') codeElement!: ElementRef;

  open(path: string, start?: number, end?: number) {
    this.filePath.set(path);
    this.startLine.set(start);
    this.endLine.set(end);
    this.language.set(path.endsWith('.json') ? 'json' : 'java');
    this.isOpen.set(true);
    this.loadCode();
  }

  close() {
    this.isOpen.set(false);
  }

  private loadCode() {
    this.loading.set(true);
    this.explorerService.getSource(this.filePath(), this.startLine(), this.endLine()).subscribe({
      next: (res) => {
        this.codeContent.set(res.content);
        this.totalLines.set(res.totalLines);
        this.loading.set(false);
        setTimeout(() => this.highlight());
      },
      error: () => {
        this.codeContent.set('// Error cargando el código fuente.');
        this.loading.set(false);
        setTimeout(() => this.highlight());
      }
    });
  }

  private highlight() {
    if (this.codeElement) {
      Prism.highlightElement(this.codeElement.nativeElement);
    }
  }

  copyCode() {
    navigator.clipboard.writeText(this.codeContent());
  }
}
