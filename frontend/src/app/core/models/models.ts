export interface ScenarioInfo {
  id: string;
  title: string;
  story: string;
  concepts: string[];
  steps: ScenarioStep[];
  bridge: BridgeInfo;
  pitfalls: string[];
  interviewQuestions: string[];
  sourceRefs: SourceRef[];
}

export interface ScenarioStep {
  stepNumber: number;
  description: string;
  request: RequestExample;
}

export interface RequestExample {
  method: string;
  url: string;
  headers: Record<string, string>;
  body: any;
  description?: string;
}

export interface BridgeInfo {
  express: string;
  aspnet: string;
}

export interface SourceRef {
  label: string;
  file: string;
  startLine?: number;
  endLine?: number;
}

export interface Product {
  id: number;
  sku: string;
  name: string;
  price: number;
  stock: number;
  categoryId: number;
  categoryName?: string;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
}

export interface Customer {
  id: number;
  name: string;
  email: string;
  type: string;
  userId?: string;
}

export interface Order {
  id: number;
  customerId: number;
  customerName?: string;
  status: string;
  subtotal: number;
  discount: number;
  total: number;
  createdAt: string;
  lines: OrderLine[];
}

export interface OrderLine {
  productId: number;
  productSku: string;
  productName: string;
  quantity: number;
  unitPrice: number;
  lineTotal: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  page: number;
}

export interface TraceData {
  traceId: string;
  startTime: string;
  steps: TraceStep[];
}

export interface TraceStep {
  component: string;
  className: string;
  detail: string;
  timestamp: string;
  durationMs: number;
}

export interface ConceptInfo {
  id: string;
  name: string;
  category: string;
  description: string;
  bridgeExpress: string;
  bridgeAspNet: string;
  scenarioIds: string[];
}

export interface ArchitectureLayer {
  id: string;
  name: string;
  description: string;
  expressEquivalent: string;
  aspnetEquivalent: string;
  components: ComponentBean[];
}

export interface ComponentBean {
  name: string;
  type: string;
  annotation: string;
  sourceFile: string;
  role: string;
}

export interface SourceCodeResponse {
  file: string;
  content: string;
  totalLines: number;
  startLine: number;
  endLine: number;
}

export interface LoginResponse {
  token: string;
  username: string;
  role: string;
  expiresIn: number;
}
