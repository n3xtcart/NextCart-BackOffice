document.addEventListener('DOMContentLoaded', function () {
  createCards();

  // Filtro per nome categoria
  const searchInput = document.getElementById('searchInput');
  searchInput.addEventListener('input', function () {
    const searchTerm = searchInput.value.trim();
    if (searchTerm === '') {
      createCards(); // Mostra tutte le card
    } else {
      filterCategoriesByName(searchTerm);
    }
  });

  addEventListeners();
});

// Dati delle categorie
let categories = [
  {
    id: 1,
    name: "Carne",
    productCount: 12,
    image: "carne.png"
  },
  {
    id: 2,
    name: "Frutta & Verdure",
    productCount: 20,
    image: "fruttaverdure.jpg"
  },
  {
    id: 3,
    name: "Pesce",
    productCount: 8,
    image: "pesce.jpg"
  },
  {
    id: 4,
    name: "Igiene",
    productCount: 15,
    image: "igiene.jpg"
  },
  {
    id: 5,
    name: "Salumi & Formaggi",
    productCount: 10,
    image: "salumiformaggi.jpg"
  },
  {
    id: 6,
    name: "Uova & Latticini",
    productCount: 25,
    image: "uovalatticini.jpeg"
  }
];

// Crea dinamicamente le card
function createCards() {
  renderFilteredCategoryCards(categories);
}

// Filtra le categorie per nome
function filterCategoriesByName(searchTerm) {
  const filteredCategories = categories.filter(category =>
    category.name.toLowerCase().includes(searchTerm.toLowerCase())
  );
  renderFilteredCategoryCards(filteredCategories);
}

// Crea le card a partire da un array di categorie (intero o filtrato)
function renderFilteredCategoryCards(categoryArray) {
  const container = document.querySelector('.row.g-4');
  container.innerHTML = '';

  categoryArray.forEach(category => {
    const card = document.createElement('div');
    card.classList.add('col-md-4', 'col-lg-3');
    card.innerHTML = `
      <div class="card category-card position-relative">
        <button type="button" class="btn-light position-absolute top-0 end-0 m-2 deleteButton" aria-label="Chiudi">
          <i class="fas fa-times text-danger"></i>
        </button>
        <button type="button" class="btn-light position-absolute top-0 start-0 m-2 editButton" aria-label="Modifica">
          <i class="fas fa-pen text-primary"></i>
        </button>
        <div class="card-body text-center">
          <img 
            src="/resources/img/home/${category.image}" 
            onerror="this.onerror=null; this.src='/resources/img/home/default.jpg';"
            class="card-img-top img-fluid" 
            style="height: 120px; object-fit: cover;" 
            alt="${category.name}">
          <h5 class="card-title">${category.name}</h5>
          <p class="card-text">${category.productCount} prodotti</p>
        </div>
      </div>
    `;
    container.appendChild(card);
  });

  addEventListeners();
}

// Event listener per modifica ed eliminazione
function addEventListeners() {
  const editButtons = document.querySelectorAll('.editButton');
  const deleteButtons = document.querySelectorAll('.deleteButton');

  // Modifica
  editButtons.forEach(button => {
    button.addEventListener('click', function () {
      const card = button.closest('.card');
      const categoryName = card.querySelector('.card-title').innerText;
      const productCount = card.querySelector('.card-text').innerText.split(' ')[0];

      document.getElementById('categoryName').value = categoryName;
      document.getElementById('productCount').value = productCount;

      document.getElementById('editPopup').style.display = 'flex';
    });
  });

  // Eliminazione
  deleteButtons.forEach(button => {
    button.addEventListener('click', function () {
      const card = button.closest('.card');
      const categoryName = card.querySelector('.card-title').innerText;

      document.getElementById('deletePopup').style.display = 'flex';

      // Conferma eliminazione
      document.getElementById('confirmDelete').onclick = function () {
        categories = categories.filter(c => c.name !== categoryName);
        console.log('Categoria eliminata:', categoryName);
        card.remove();
        document.getElementById('deletePopup').style.display = 'none';
      };

      // Annulla eliminazione
      document.getElementById('closeDelete').onclick = function () {
        document.getElementById('deletePopup').style.display = 'none';
      };
    });
  });

  // Chiudi popup modifica
  document.getElementById('closeEdit').onclick = function () {
    document.getElementById('editPopup').style.display = 'none';
  };

  // Salva modifica
  document.getElementById('saveEdit').onclick = function () {
    const categoryName = document.getElementById('categoryName').value;
    const productCount = document.getElementById('productCount').value;

    const categoryToUpdate = categories.find(c => c.name === categoryName);
    if (categoryToUpdate) {
      categoryToUpdate.productCount = productCount;
      console.log('Categoria modificata:', categoryName, 'Numero di prodotti:', productCount);
    }

    document.getElementById('editPopup').style.display = 'none';
    createCards(); // Ricarica le card
  };
}


