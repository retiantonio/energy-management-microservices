import { HttpErrorResponse } from '@angular/common/http';

export interface HttpFieldError {
  field: string;
  message: string;
}

export interface HttpErrorDetails {
  message: string;
  fieldErrors: HttpFieldError[];
}

export function getHttpErrorDetails(
  error: unknown,
  fallbackMessage: string,
  unavailableMessage = 'Could not reach the service. Check that the services are running.',
): HttpErrorDetails {
  if (!(error instanceof HttpErrorResponse)) {
    return { message: fallbackMessage, fieldErrors: [] };
  }

  const body: unknown = error.error;
  const message =
    isRecord(body) && typeof body['message'] === 'string' && body['message'].trim()
      ? body['message']
      : '';
  const fieldErrors = getFieldErrorMessages(body);

  if (fieldErrors.length > 0) {
    return {
      message: message || 'One or more fields are invalid',
      fieldErrors,
    };
  }

  if (message) {
    return { message, fieldErrors: [] };
  }

  if (error.status === 0) {
    return { message: unavailableMessage, fieldErrors: [] };
  }

  return { message: `Request failed (HTTP ${error.status}).`, fieldErrors: [] };
}

function getFieldErrorMessages(body: unknown): HttpFieldError[] {
  if (isRecord(body) && isRecord(body['fieldErrors'])) {
    return Object.entries(body['fieldErrors'])
      .filter(
        (entry): entry is [string, string] => typeof entry[1] === 'string' && !!entry[1].trim(),
      )
      .map(([field, message]) => ({ field: formatFieldName(field), message }));
  }

  return [];
}

function formatFieldName(field: string): string {
  return field
    .replace(/([a-z0-9])([A-Z])/g, '$1 $2')
    .replace(/[_-]+/g, ' ')
    .replace(/^./, (character) => character.toLocaleUpperCase());
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}
