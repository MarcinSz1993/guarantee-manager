import {Component, OnInit} from '@angular/core';
import {DatePipe, NgClass, NgForOf, NgIf} from '@angular/common';
import {GuaranteeHistoryControllerService} from '../../services/services/guarantee-history-controller.service';
import {PageGuaranteeHistoryResponse} from '../../services/models/page-guarantee-history-response';
import {AddHistoryComponent} from '../../modals/add-history/add-history.component';
import {ApiResponse} from '../../services/models/api-response';
import {ToastrService} from 'ngx-toastr';
import {EmptyArchiveComponent} from '../empty-archive/empty-archive.component';

@Component({
  selector: 'app-archive',
  imports: [
    DatePipe,
    NgClass,
    NgForOf,
    AddHistoryComponent,
    NgIf,
    EmptyArchiveComponent
  ],
  templateUrl: './archive.component.html',
  standalone: true,
  styleUrl: './archive.component.scss'
})
export class ArchiveComponent implements OnInit{
  allGuaranteesHistories: PageGuaranteeHistoryResponse = {};
  errorMsg:string = '';
  isAddGuaranteeHistoryModalVisible = false;
  deleteGuaranteeHistoryResponse: ApiResponse = {};
  currentPage:number = 1;
  totalPages:number = 0;

  constructor(
    private guaranteeHistoryService: GuaranteeHistoryControllerService,
    private toastrService: ToastrService
  ) {
  }

  ngOnInit(): void {
        this.fetchGuaranteesHistories();
    }

    fetchGuaranteesHistories(){
      this.guaranteeHistoryService.getAllUserGuaranteesHistories({
        pageNumber:this.currentPage -1
      }).subscribe({
        next:(result)=>{
          this.allGuaranteesHistories = result;
          this.totalPages = result.totalPages as number;
          console.log(result.content);
        },
        error:(err)=>{
          this.errorMsg = err.error.message;
        }}
      );
    }

  closeModal() {
    this.isAddGuaranteeHistoryModalVisible = false;
  }

  onSubmitData() {
    this.fetchGuaranteesHistories();
  }

  openModal() {
    this.isAddGuaranteeHistoryModalVisible = true;
  }

  deleteGuaranteeHistory(guaranteeHistoryId: number) {
    const confirmDelete = window.confirm("Are you sure?");
    if (!confirmDelete){
      return;
    }
    this.guaranteeHistoryService.deleteGuaranteeHistory({
      guaranteeHistoryId:guaranteeHistoryId
    }).subscribe({
      next:(result)=>{
        this.deleteGuaranteeHistoryResponse = result;
        this.toastrService.success(this.deleteGuaranteeHistoryResponse.message,'',{
          positionClass: 'toast-center-center'
        });
        this.fetchGuaranteesHistories();
      },
      error:(err)=>{
        this.errorMsg = err.error.message;
        this.toastrService.error(this.errorMsg,'',{
          positionClass: 'toast-center-center'
        });
      }
    });
  }

  previousPage() {
    if (this.currentPage > 1){
      this.currentPage--;
      this.fetchGuaranteesHistories();
    }

  }

  nextPage() {
    if (this.currentPage === this.totalPages){
      return;
    }
    this.currentPage++;
    this.fetchGuaranteesHistories();
  }
}
