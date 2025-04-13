import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoGuaranteesComponent } from './no-guarantees.component';

describe('NoGuaranteesComponent', () => {
  let component: NoGuaranteesComponent;
  let fixture: ComponentFixture<NoGuaranteesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoGuaranteesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NoGuaranteesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
