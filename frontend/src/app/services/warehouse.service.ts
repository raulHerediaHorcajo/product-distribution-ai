import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Warehouse } from '@models/warehouse.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class WarehouseService {
  private readonly api = `${environment.apiUrl}/warehouses`;
  private readonly http = inject(HttpClient);

  getAll(): Observable<Warehouse[]> {
    return this.http.get<Warehouse[]>(this.api);
  }
}
