# TradeBlock
### Features
* **Login**
  * Attempt to login with an email and password.  An error will display, notifying you that you need to register.  If you were registered, it will notify you if you're using an incorrect password.
* **Register**
  * Click the "Create Account" button.
  * Register for an account.  Once registered, it will automatically log you in to your profile.  Enter an invalid email address (email.1, for example), and you'll be able to see the email validation.  You can also enter a weak password (i.e. 123abc) to see the password validation.
* **Navigation Drawer**
  * Click the hamburger icon in the top left.
  * You can select any of the Home, Gallery, or Slideshow fragments and they'll take you to a new screen.  They're empty for now, and only placeholders, but the skeleton is there.
  * You can hit the logout button to, umm, logout. If you do, you can attempt to use an incorrect password to see the errors.
  * You may also select the "Forgot Password" button, where it will send a password reset email to the email you registered with.
* **Account**
  * Click the "Account" item in the Navigation Drawer.
  * You can click any of the buttons you see on the "Update Profile" screen.
  * **Avatar**
    * Click the Avatar button, find an image on Google, copy its URL, and paste it into the text field.  Click Select Image and the image should appear above.  Once you're satisfied with the Avatar you've selected, you can click Update.  If you're finished, you can click Back, open the Navigation Drawer again, and view your new Avatar image in the top left.
  * **Display Name**
    * Works the same as above, but you just have to enter a new display name.  You can view the update in the Navigation Drawer like before.
  * **Email**
    * Attempt to update your email.  Updating an email is a sensitive operation and requires recent credentials -- so, if it works, that means you've logged in recently and your credentials are still valid.  To view the re-authentication functionality, you'll need to wait a bit.  If you need to re-authenticate, you'll be notified that you'll need to re-enter your current password due to stale credentials and a password text field will appear.  If you enter the correct password, your update will proceed. Again, you can view a successful update in the Navigation Drawer.
  * **Password**
    * Another sensitive operation.  You can enter in your new password, as well as confirm the password you just entered, and it will check to make sure they're both the same.  It will also validate your new password - making sure it's strong enough.  Once validated, it will update if your credentials are valid.  If not, you'll need to enter your current password.
