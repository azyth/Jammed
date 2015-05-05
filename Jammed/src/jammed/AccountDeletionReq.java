package jammed;

/*
 * AccountDeletionReq subclass of Requests that communicates that the user
 * (yes, really) wants to delete their account.
 */
public class AccountDeletionReq extends Request {

  private static final long serialVersionUID = 7L;

  // request
  public AccountDeletionReq() {
    this.setType(MessageType.REQUEST);
    this.setEvent(EventType.DELETION);
  }

  // response
  public AccountDeletionReq(boolean success, ErrorMessage err) {
    this.setType(MessageType.RESPONSE);
    this.setSuccess(success);
    this.setError(err);
    this.setEvent(EventType.DELETION);
  }
}
