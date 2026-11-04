import { Component, input } from '@angular/core';
import { HttpErrorDetails } from '../../utils/http-error-message';

@Component({
  selector: 'app-http-error-notice',
  styleUrl: './http-error-notice.scss',
  templateUrl: './http-error-notice.html',
})
export class HttpErrorNotice {
  readonly details = input.required<HttpErrorDetails>();
}
