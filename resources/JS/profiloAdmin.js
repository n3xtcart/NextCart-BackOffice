document.addEventListener('DOMContentLoaded', function () {
    const logoutButton = document.getElementById('logoutButton');
    const logoutPopup = document.getElementById('logoutPopup');
    const confirmLogout = document.getElementById('confirmLogout');
    const cancelLogout = document.getElementById('cancelLogout');
  
    // Mostra il popup quando si clicca sul bottone "LOGOUT"
    logoutButton.addEventListener('click', function (event) {
      event.preventDefault(); // Evita che il bottone faccia il suo comportamento di default
      logoutPopup.style.display = 'flex'; // Mostra il popup
    });
  
    // Nascondi il popup quando si clicca su "Annulla"
    cancelLogout.addEventListener('click', function () {
      logoutPopup.style.display = 'none'; // Nascondi il popup
    });
  
    // Reindirizza alla pagina di login quando si conferma il logout
    confirmLogout.addEventListener('click', function () {
      logoutPopup.style.display = 'none'; // Nascondi il popup
      window.location.replace('.../login.html'); // Prova con replace
    });
  });
  