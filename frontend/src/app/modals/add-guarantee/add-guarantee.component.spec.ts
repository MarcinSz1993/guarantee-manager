import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddGuaranteeComponent } from './add-guarantee.component';

describe('AddGuaranteeComponent', () => {
  let component: AddGuaranteeComponent;
  let fixture: ComponentFixture<AddGuaranteeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddGuaranteeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddGuaranteeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
