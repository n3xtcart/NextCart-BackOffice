// Dati dei prodotti con categoria e immagine inclusa
const prodotti = [
  { name: "Agnello", category: "Carne", quantity: 1000, image: "agnello.jpg" },
  { name: "Macinato", category: "Carne", quantity: 1000, image: "macinato.jpg" },
  { name: "Manzo", category: "Carne", quantity: 1000, image: "manzo.jpg" },
  { name: "Petto di Pollo", category: "Carne", quantity: 1000, image: "pettopollo.jpg" },
  { name: "Vitello", category: "Carne", quantity: 1000, image: "vitello.png" },
  { name: "Bagnoschiuma", category: "Igiene", quantity: 1000, image: "bagnoschiuma.jpg" },
  { name: "Balsamo", category: "Igiene", quantity: 1000, image: "balsamo.jpg" },
  { name: "Sapone per Mani", category: "Igiene", quantity: 1000, image: "saponemani.jpg" },
  { name: "Saponetta", category: "Igiene", quantity: 1000, image: "saponetta.jpg" },
  { name: "Shampoo", category: "Igiene", quantity: 1000, image: "shampoo.jpg" },
  { name: "Pesce Spada", category: "Pesce", quantity: 1000, image: "spada.jpg" },
  { name: "Salmone", category: "Pesce", quantity: 1000, image: "salmone.jpg" },
  { name: "Sogliola", category: "Pesce", quantity: 1000, image: "sogliola.jpg" },
  { name: "Orata", category: "Pesce", quantity: 1000, image: "orata.jpg" },
  { name: "Spigola", category: "Pesce", quantity: 1000, image: "spigola.jpg" },
  { name: "Uova", category: "UovaLatticini", quantity: 1000, image: "uova.jpg" },
  { name: "Ricotta", category: "UovaLatticini", quantity: 1000, image: "ricotta.jpg" },
  { name: "Yogurt", category: "UovaLatticini", quantity: 1000, image: "yogurt.jpg" },
  { name: "Mozzarella", category: "UovaLatticini", quantity: 1000, image: "mozzarella.jpg" },
  { name: "Provola", category: "UovaLatticini", quantity: 1000, image: "provola.jpg" },
  { name: "Iceberg", category: "FruttaVerdure", quantity: 1000, image: "iceberg.jpg" },
  { name: "Pomodori", category: "FruttaVerdure", quantity: 1000, image: "pomodoro.jpg" },
  { name: "Zucchine", category: "FruttaVerdure", quantity: 1000, image: "zucchina.jpg" },
  { name: "Kiwi", category: "FruttaVerdure", quantity: 1000, image: "kiwi.jpg" },
  { name: "Limoni", category: "FruttaVerdure", quantity: 1000, image: "limoni.jpg" }
];

let currentEditingCard = null;

function filterProducts() {
  const nome = document.getElementById('nomeProdotto').value.toLowerCase();
  const quantita = parseInt(document.getElementById('quantita').value);
  const categoria = document.getElementById('categoriaSelect').value;

  const prodottiFiltrati = prodotti.filter(p => {
    const matchNome = !nome || p.name.toLowerCase().includes(nome);
    const matchQuantita = isNaN(quantita) || p.quantity === quantita;
    const matchCategoria = !categoria || p.category === categoria;
    return matchNome && matchQuantita && matchCategoria;
  });

  createProductCards(prodottiFiltrati);
}

function createProductCards(prodottiDaMostrare = prodotti) {
  const container = document.querySelector('.row.g-4');
  container.innerHTML = '';

  prodottiDaMostrare.forEach(product => {
    const card = document.createElement('div');
    card.classList.add('col-md-4', 'col-lg-3');
    card.innerHTML = `
      <div class="card product-card position-relative">
        <button type="button" class="btn-light position-absolute top-0 end-0 m-2 deleteButton" aria-label="Chiudi">
          <i class="fas fa-times text-danger"></i>
        </button>
        <button type="button" class="btn-light position-absolute top-0 start-0 m-2 editButton" aria-label="Modifica">
          <i class="fas fa-pen text-primary"></i>
        </button>
        <div class="card-body text-center">
          <img 
            src="/resources/img/home/${product.category}/${product.image}" 
            onerror="this.onerror=null; this.src='/resources/img/home/default.jpg';"
            class="card-img-top img-fluid" 
            style="height: 120px; object-fit: cover;" 
            alt="${product.name}">
          <h5 class="card-title">${product.name}</h5>
          <p class="card-text">Quantità:<br>${product.quantity}</p>
        </div>
      </div>
    `;
    container.appendChild(card);
  });

  addProductEventListeners();
}

function addProductEventListeners() {
  const editButtons = document.querySelectorAll('.editButton');
  const deleteButtons = document.querySelectorAll('.deleteButton');

  editButtons.forEach(button => {
    button.addEventListener('click', function () {
      const card = button.closest('.card');
      const productName = card.querySelector('.card-title').innerText;
      const productQuantity = card.querySelector('.card-text').innerText.split('\n')[1];

      document.getElementById('productName').value = productName;
      document.getElementById('productQuantity').value = productQuantity;

      currentEditingCard = card;
      document.getElementById('editProductPopup').style.display = 'flex';
    });
  });

  deleteButtons.forEach(button => {
    button.addEventListener('click', function () {
      const card = button.closest('.card');
      const productName = card.querySelector('.card-title').innerText;

      document.getElementById('deleteProductPopup').style.display = 'flex';

      const confirmBtn = document.getElementById('confirmDeleteProduct');
      const cancelBtn = document.getElementById('closeDeleteProduct');

      const confirmHandler = function () {
        console.log('Prodotto eliminato:', productName);
        card.remove();
        document.getElementById('deleteProductPopup').style.display = 'none';

        confirmBtn.removeEventListener('click', confirmHandler);
        cancelBtn.removeEventListener('click', cancelHandler);
      };

      const cancelHandler = function () {
        document.getElementById('deleteProductPopup').style.display = 'none';
        confirmBtn.removeEventListener('click', confirmHandler);
        cancelBtn.removeEventListener('click', cancelHandler);
      };

      confirmBtn.addEventListener('click', confirmHandler);
      cancelBtn.addEventListener('click', cancelHandler);
    });
  });

  document.getElementById('closeEditProduct').addEventListener('click', function () {
    document.getElementById('editProductPopup').style.display = 'none';
  });

  document.getElementById('saveEditProduct').addEventListener('click', function () {
    const newName = document.getElementById('productName').value;
    const newQuantity = document.getElementById('productQuantity').value;

    if (currentEditingCard) {
      currentEditingCard.querySelector('.card-title').innerText = newName;
      currentEditingCard.querySelector('.card-text').innerHTML = `Quantità:<br>${newQuantity}`;
    }

    console.log('Prodotto modificato:', newName, 'Quantità:', newQuantity);
    document.getElementById('editProductPopup').style.display = 'none';
  });
}

document.addEventListener('DOMContentLoaded', function () {
  createProductCards();

  document.getElementById('nomeProdotto').addEventListener('input', filterProducts);
  document.getElementById('quantita').addEventListener('input', filterProducts);
  document.querySelectorAll('input[name="unita"]').forEach(radio => {
    radio.addEventListener('change', filterProducts);
  });
  document.getElementById('categoriaSelect').addEventListener('change', filterProducts);
});
