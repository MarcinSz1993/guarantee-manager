import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteGuaranteeComponent } from './delete-guarantee.component';

describe('DeleteGuaranteeComponent', () => {
  let component: DeleteGuaranteeComponent;
  let fixture: ComponentFixture<DeleteGuaranteeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteGuaranteeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeleteGuaranteeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
