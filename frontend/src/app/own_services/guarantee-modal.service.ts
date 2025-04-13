import { Injectable } from '@angular/core';
import {Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GuaranteeModalService {
  private guaranteeAddedSource = new Subject<void>();
  private guaranteeDeletedSource = new Subject<void>();

  guaranteeAdded$ = this.guaranteeAddedSource.asObservable();
  guaranteeDeleted$ = this.guaranteeDeletedSource.asObservable();

  notifyGuaranteeAdded(){
    this.guaranteeAddedSource.next();
  }

  notifyGuaranteeDeleted(){
    this.guaranteeDeletedSource.next();
  }
}
