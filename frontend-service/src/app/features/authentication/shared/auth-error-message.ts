import {
  getHttpErrorDetails,
} from '../../../core/utils/http-error-message';

export function getAuthErrorDetails(error: unknown) {
  return getHttpErrorDetails(
    error,
    'An unexpected authentication error occurred.',
    'Could not reach the authentication service. Check that the services are running.',
  );
}
