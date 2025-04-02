import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangePreferenceComponent } from './change-preference.component';

describe('ChangePreferenceComponent', () => {
  let component: ChangePreferenceComponent;
  let fixture: ComponentFixture<ChangePreferenceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangePreferenceComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangePreferenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
