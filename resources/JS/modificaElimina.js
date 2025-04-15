document.addEventListener('DOMContentLoaded', function() {
    // Seleziona tutti i pulsanti di modifica e di eliminazione
    const editButtons = document.querySelectorAll('.editButton');
    const deleteButtons = document.querySelectorAll('.deleteButton');
  
    // Aggiungi gli event listener ai pulsanti di modifica
    editButtons.forEach(button => {
      button.addEventListener('click', function() {
        // Mostra il popup di modifica
        document.getElementById('editPopup').style.display = 'flex';
      });
    });
  
    // Aggiungi gli event listener ai pulsanti di eliminazione
    deleteButtons.forEach(button => {
      button.addEventListener('click', function() {
        // Mostra il popup di eliminazione
        document.getElementById('deletePopup').style.display = 'flex';
      });
    });
  
    // Gestione chiusura popup di modifica
    document.getElementById('closeEdit').addEventListener('click', function() {
      document.getElementById('editPopup').style.display = 'none';
    });
  
    // Gestione chiusura popup di eliminazione
    document.getElementById('closeDelete').addEventListener('click', function() {
      document.getElementById('deletePopup').style.display = 'none';
    });
  
    // Gestione salvataggio modifiche
    document.getElementById('saveEdit').addEventListener('click', function() {
      const categoryName = document.getElementById('categoryName').value;
      const productCount = document.getElementById('productCount').value;
      
      // Gestione dell'immagine
      const categoryImage = document.getElementById('categoryImage').files[0];
      if (categoryImage) {
        const reader = new FileReader();
        reader.onload = function(event) {
          console.log('Immagine caricata:', event.target.result);
        };
        reader.readAsDataURL(categoryImage);
      }
      
      console.log('Categoria modificata:', categoryName, 'Numero di prodotti:', productCount);
      document.getElementById('editPopup').style.display = 'none';
    });
  
    // Gestione eliminazione
    document.getElementById('confirmDelete').addEventListener('click', function() {
      console.log('Categoria eliminata');
      document.getElementById('deletePopup').style.display = 'none';
    });
  });
  
  