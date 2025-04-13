import { TestBed } from '@angular/core/testing';

import { GuaranteeModalService } from './guarantee-modal.service';

describe('GuaranteeService', () => {
  let service: GuaranteeModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GuaranteeModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
