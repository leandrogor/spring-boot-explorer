import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExplorerService } from '../../core/services/explorer.service';
import { Product, Order } from '../../core/models/models';

@Component({
  selector: 'app-business-state',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="state-page">
      <div class="page-header">
        <div>
          <h1>📊 Estado del Negocio en Memoria</h1>
          <p class="subtitle">
            Visualizá los datos vivos en el backend (ConcurrentHashMap) para observar cómo cada request reserva stock o cambia el ciclo de vida del pedido.
          </p>
        </div>
        <div class="header-actions">
          <button class="btn btn-outline btn-sm" (click)="loadData()">
            🔄 Refrescar Estado
          </button>
        </div>
      </div>

      <div class="state-grid">
        <!-- Products Table -->
        <div class="state-card">
          <div class="card-header">
            <h3>📦 Catálogo de Productos ({{ products().length }})</h3>
            <span class="badge badge-primary">ConcurrentHashMap</span>
          </div>
          
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>SKU</th>
                  <th>Producto</th>
                  <th>Categoría</th>
                  <th>Precio Unit.</th>
                  <th>Stock Actual</th>
                </tr>
              </thead>
              <tbody>
                @for (prod of products(); track prod.id) {
                  <tr>
                    <td><strong>#{{ prod.id }}</strong></td>
                    <td><code>{{ prod.sku }}</code></td>
                    <td>{{ prod.name }}</td>
                    <td><span class="cat-pill">{{ prod.categoryName || 'Cat ' + prod.categoryId }}</span></td>
                    <td class="price">\${{ prod.price | number:'1.2-2' }}</td>
                    <td>
                      <span class="stock-pill" [ngClass]="getStockClass(prod.stock)">
                        {{ prod.stock }} {{ prod.stock === 1 ? 'unidad' : 'unidades' }}
                      </span>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>

        <!-- Orders Table -->
        <div class="state-card">
          <div class="card-header">
            <h3>🛒 Pedidos Registrados ({{ orders().length }})</h3>
            <span class="badge badge-secondary">Ciclo de Vida</span>
          </div>
          
          <div class="orders-list">
            @for (order of orders(); track order.id) {
              <div class="order-item">
                <div class="order-item-header">
                  <div class="order-id-group">
                    <span class="order-id">Pedido #{{ order.id }}</span>
                    <span class="customer-name">{{ order.customerName || 'Cliente #' + order.customerId }}</span>
                  </div>
                  <span class="order-status-pill" [ngClass]="order.status.toLowerCase()">
                    {{ order.status }}
                  </span>
                </div>

                <div class="order-lines-box">
                  <div class="lines-title">Líneas del pedido:</div>
                  @for (line of order.lines; track line.productId) {
                    <div class="line-row">
                      <span>{{ line.quantity }}x {{ line.productName || line.productSku }}</span>
                      <span class="line-price">\${{ line.lineTotal | number:'1.2-2' }}</span>
                    </div>
                  }
                </div>

                <div class="order-totals">
                  <div class="subtotal-info">
                    @if (order.discount && order.discount > 0) {
                      <span>Subtotal: \${{ order.subtotal | number:'1.2-2' }} | Desc: -\${{ order.discount | number:'1.2-2' }}</span>
                    }
                  </div>
                  <div class="total-info">
                    Total: <strong>\${{ order.total | number:'1.2-2' }}</strong>
                  </div>
                </div>
              </div>
            } @empty {
              <div class="empty-state">No hay pedidos registrados en memoria.</div>
            }
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .state-page { max-width: 1300px; margin: 0 auto; padding: 1.5rem; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 2rem; gap: 1rem; }
    .page-header h1 { margin: 0 0 0.5rem 0; font-size: 1.75rem; color: #f8fafc; }
    .subtitle { margin: 0; color: #94a3b8; font-size: 0.95rem; }
    .header-actions { flex-shrink: 0; }

    .state-grid { display: grid; grid-template-columns: 1.2fr 1fr; gap: 1.5rem; }
    @media (max-width: 1000px) { .state-grid { grid-template-columns: 1fr; } }

    .state-card {
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      border-radius: 10px;
      padding: 1.25rem;
      display: flex;
      flex-direction: column;
    }
    .card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .card-header h3 { margin: 0; font-size: 1.15rem; color: #f1f5f9; }

    .table-wrap { overflow-x: auto; }
    table { width: 100%; border-collapse: collapse; font-size: 0.85rem; }
    th { text-align: left; padding: 0.65rem 0.5rem; color: #64748b; font-weight: 600; border-bottom: 1px solid var(--border-color); font-size: 0.75rem; text-transform: uppercase; }
    td { padding: 0.75rem 0.5rem; border-bottom: 1px solid var(--border-color); color: #cbd5e1; }
    code { font-family: var(--font-mono); color: var(--primary); background: rgba(56, 189, 248, 0.1); padding: 0.15rem 0.35rem; border-radius: 4px; font-size: 0.8rem; }
    .cat-pill { font-size: 0.75rem; color: #94a3b8; background: #334155; padding: 0.15rem 0.4rem; border-radius: 4px; }
    .price { font-family: var(--font-mono); font-weight: 600; color: #f8fafc; }

    .stock-pill { font-size: 0.75rem; font-weight: 700; padding: 0.2rem 0.5rem; border-radius: 4px; }
    .stock-high { background: #065f46; color: #a7f3d0; }
    .stock-low { background: #854d0e; color: #fef08a; }
    .stock-zero { background: #7f1d1d; color: #fecaca; }

    .orders-list { display: flex; flex-direction: column; gap: 1rem; }
    .order-item {
      background: var(--bg-main);
      border: 1px solid var(--border-color);
      border-radius: 8px;
      padding: 1rem;
    }
    .order-item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem; }
    .order-id-group { display: flex; align-items: center; gap: 0.5rem; }
    .order-id { font-weight: 700; font-size: 0.9rem; color: #f8fafc; }
    .customer-name { font-size: 0.8rem; color: #94a3b8; }

    .order-status-pill {
      font-size: 0.75rem;
      font-weight: 700;
      padding: 0.2rem 0.5rem;
      border-radius: 4px;
      text-transform: uppercase;
    }
    .order-status-pill.draft { background: #334155; color: #94a3b8; }
    .order-status-pill.confirmed { background: #075985; color: #bae6fd; }
    .order-status-pill.paid { background: #5b21b6; color: #ddd6fe; }
    .order-status-pill.shipped { background: #854d0e; color: #fef08a; }
    .order-status-pill.delivered { background: #065f46; color: #a7f3d0; }
    .order-status-pill.cancelled { background: #7f1d1d; color: #fecaca; }

    .order-lines-box { background: var(--bg-card); border-radius: 6px; padding: 0.5rem 0.75rem; margin-bottom: 0.75rem; }
    .lines-title { font-size: 0.75rem; color: #64748b; font-weight: 600; margin-bottom: 0.35rem; }
    .line-row { display: flex; justify-content: space-between; font-size: 0.8rem; color: #cbd5e1; margin-bottom: 0.2rem; }
    .line-price { font-family: var(--font-mono); color: #f8fafc; }

    .order-totals { display: flex; justify-content: space-between; align-items: center; font-size: 0.85rem; padding-top: 0.25rem; }
    .subtotal-info { font-size: 0.75rem; color: #94a3b8; }
    .total-info { font-size: 0.95rem; color: #f8fafc; }
    .total-info strong { color: var(--primary); font-family: var(--font-mono); }

    .empty-state { text-align: center; padding: 3rem; color: #64748b; font-size: 0.875rem; }
  `]
})
export class BusinessStateComponent implements OnInit {
  private explorerService = inject(ExplorerService);
  
  products = signal<Product[]>([]);
  orders = signal<Order[]>([]);

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.explorerService.getProducts().subscribe(res => {
      this.products.set(res.content || []);
    });
    this.explorerService.getOrders().subscribe(res => {
      this.orders.set(res || []);
    });
  }

  getStockClass(stock: number): string {
    if (stock > 10) return 'stock-high';
    if (stock > 0) return 'stock-low';
    return 'stock-zero';
  }
}
