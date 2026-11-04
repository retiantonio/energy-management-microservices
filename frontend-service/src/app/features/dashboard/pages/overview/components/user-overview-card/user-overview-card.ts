import { Component, Input } from '@angular/core';
import { User } from '../../../../../users/models/user.models';

@Component({
  selector: 'app-user-overview-card',
  styleUrl: './user-overview-card.scss',
  templateUrl: './user-overview-card.html',
})
export class UserOverviewCard {
  @Input({ required: true }) user!: User;
}
