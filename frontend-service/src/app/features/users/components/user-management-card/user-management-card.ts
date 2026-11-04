import { Component, EventEmitter, Input, Output } from '@angular/core';
import { User } from '../../models/user.models';

@Component({
  selector: 'app-user-management-card',
  styleUrl: './user-management-card.scss',
  templateUrl: './user-management-card.html',
})
export class UserManagementCard {
  @Input({ required: true }) user!: User;
  @Input() isSaving = false;

  @Output() edit = new EventEmitter<void>();
  @Output() delete = new EventEmitter<void>();
}
