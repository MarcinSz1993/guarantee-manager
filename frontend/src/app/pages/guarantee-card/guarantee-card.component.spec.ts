import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuaranteeCardComponent } from './guarantee-card.component';

describe('GuaranteeCardComponent', () => {
  let component: GuaranteeCardComponent;
  let fixture: ComponentFixture<GuaranteeCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuaranteeCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GuaranteeCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
