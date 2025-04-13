import {Component, OnInit} from '@angular/core';
import {AddHistoryComponent} from '../../modals/add-history/add-history.component';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-empty-archive',
  imports: [
    AddHistoryComponent,
    NgIf
  ],
  templateUrl: './empty-archive.component.html',
  standalone: true,
  styleUrl: './empty-archive.component.scss'
})
export class EmptyArchiveComponent implements OnInit{
  isAddGuaranteeModalVisible = false;

  ngOnInit(): void {
      throw new Error('Method not implemented.');
  }

  openAddGuaranteeHistoryModal() {
    this.isAddGuaranteeModalVisible = true;
  }

  closeAddGuaranteeHistoryModal() {
    this.isAddGuaranteeModalVisible = false;
  }
}
