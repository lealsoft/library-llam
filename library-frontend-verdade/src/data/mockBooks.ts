export const BOOKS = [
  {
    id: 1, title: 'Dom Casmurro', author: 'Machado de Assis', category: 'Literatura Brasileira',
    isbn: '978-85-359-0277-5', editora: 'Ática', ano: '1899 (ed. 2023)', paginas: '256',
    idioma: 'Português', edicao: '3ª edição',
    cover: 'linear-gradient(135deg,#1A2A1A,#0F6E56)',
    avail: 3, total: 5, digital: true, price: 'R$ 12,90',
    sinopse: 'Narrado em primeira pessoa por Bentinho, o romance questiona a fidelidade de sua amada Capitu e explora temas como ciúme, memória e subjetividade da percepção humana. Uma das obras mais importantes do Realismo brasileiro, Dom Casmurro permanece atual em suas reflexões sobre confiança e a natureza traiçoeira da memória.',
    exemplares: [
      { code: '978-85-359-0277-001', loc: 'Seção A · Corredor 2 · Prateleira 4', status: 'ok', label: 'Disponível' },
      { code: '978-85-359-0277-002', loc: 'Seção A · Corredor 2 · Prateleira 4', status: 'emp', label: 'Emprestado' },
      { code: '978-85-359-0277-003', loc: 'Seção A · Corredor 2 · Prateleira 5', status: 'ok', label: 'Disponível' },
      { code: '978-85-359-0277-004', loc: 'Seção A · Corredor 2 · Prateleira 5', status: 'ok', label: 'Disponível' },
      { code: '978-85-359-0277-005', loc: 'Seção A · Corredor 3 · Prateleira 1', status: 'proc', label: 'Em processamento' }
    ]
  },
  {
    id: 2, title: 'Clean Code', author: 'Robert C. Martin', category: 'Tecnologia',
    isbn: '978-01-3235-088-0', editora: 'Prentice Hall', ano: '2008', paginas: '431',
    idioma: 'Inglês', edicao: '1ª edição',
    cover: 'linear-gradient(135deg,#1A1A2A,#185FA5)',
    avail: 1, total: 3, digital: true, price: 'R$ 18,90',
    sinopse: 'Um guia definitivo sobre boas práticas de programação. Martin apresenta princípios, padrões e técnicas para escrever código limpo... Leitura obrigatória.',
    exemplares: [
      { code: '978-01-3235-088-001', loc: 'Seção B · Corredor 1 · Prateleira 2', status: 'emp', label: 'Emprestado' },
      { code: '978-01-3235-088-002', loc: 'Seção B · Corredor 1 · Prateleira 2', status: 'emp', label: 'Emprestado' },
      { code: '978-01-3235-088-003', loc: 'Seção B · Corredor 1 · Prateleira 3', status: 'ok', label: 'Disponível' }
    ]
  },
  {
    id: 3, title: 'O Alquimista', author: 'Paulo Coelho', category: 'Literatura Brasileira',
    isbn: '978-85-325-0101-8', editora: 'HarperCollins', ano: '1988', paginas: '197',
    cover: 'linear-gradient(135deg,#2A1A10,#993C1D)',
    idioma: 'Português', edicao: 'Especial',
    avail: 0, total: 6, digital: true, price: 'R$ 12,90',
    sinopse: 'A história de Santiago, um jovem pastor andaluz que sonha em descobrir um tesouro no Egito.',
    exemplares: [
      { code: '978-85-325-0101-001', loc: 'Seção A · Corredor 5 · Prateleira 1', status: 'emp', label: 'Emprestado' },
      { code: '978-85-325-0101-002', loc: 'Seção A · Corredor 5 · Prateleira 1', status: 'emp', label: 'Emprestado' },
    ]
  },
  {
    id: 4, title: 'Design Patterns', author: 'Gang of Four', category: 'Tecnologia',
    isbn: '978-02-0163-483-7', editora: 'Addison-Wesley', ano: '1994', paginas: '395',
    idioma: 'Inglês', edicao: '1ª edição',
    cover: 'linear-gradient(135deg,#1A1220,#534AB7)',
    avail: 2, total: 4, digital: false, price: null,
    sinopse: 'O livro clássico sobre padrões de projeto de software. Os autores catalogam 23 padrões recorrentes.',
    exemplares: [
      { code: '978-02-0163-483-001', loc: 'Seção B · Corredor 2 · Prateleira 1', status: 'ok', label: 'Disponível' },
      { code: '978-02-0163-483-002', loc: 'Seção B · Corredor 2 · Prateleira 1', status: 'emp', label: 'Emprestado' },
    ]
  },
  {
    id: 5, title: 'Java Efetivo', author: 'Joshua Bloch', category: 'Tecnologia',
    cover: 'linear-gradient(135deg,#1A1020,#72243E)',
    isbn: 'xxx', editora: 'Addison-Wesley', ano: '2018', paginas: '300', idioma: 'Inglês', edicao: '3ª edição',
    avail: 0, total: 2, digital: true, price: 'R$ 22,90',
    sinopse: 'A bíblia do desenvolvedor Java...', exemplares: []
  },
  {
    id: 6, title: 'Capitães da Areia', author: 'Jorge Amado', category: 'Literatura Brasileira',
    cover: 'linear-gradient(135deg,#1A1A14,#3B6D11)',
    isbn: 'xxx', editora: 'Companhia', ano: '1937', paginas: '200', idioma: 'Português', edicao: '1',
    avail: 5, total: 5, digital: false, price: null, sinopse: '...', exemplares: []
  },
  {
    id: 7, title: 'The Pragmatic Programmer', author: 'Hunt & Thomas', category: 'Tecnologia',
    cover: 'linear-gradient(135deg,#1A1010,#A32D2D)',
    isbn: 'xxx', editora: 'Addison-Wesley', ano: '2019', paginas: '300', idioma: 'Inglês', edicao: '2',
    avail: 0, total: 1, digital: true, price: 'R$ 24,90', sinopse: '...', exemplares: []
  },
  {
    id: 8, title: 'Refactoring', author: 'Martin Fowler', category: 'Tecnologia',
    cover: 'linear-gradient(135deg,#10181A,#085041)',
    isbn: 'xxx', editora: 'Addison-Wesley', ano: '2019', paginas: '448', idioma: 'Inglês', edicao: '2',
    avail: 0, total: 2, digital: false, price: null, sinopse: '...', exemplares: []
  },
  {
    id: 9, title: 'Memórias Póstumas', author: 'Machado de Assis', category: 'Literatura Brasileira',
    cover: 'linear-gradient(135deg,#14141A,#2A3444)',
    isbn: 'xxx', editora: 'Ática', ano: '1881', paginas: '208', idioma: 'Português', edicao: '2',
    avail: 2, total: 3, digital: false, price: null, sinopse: '...', exemplares: []
  },
  {
    id: 10, title: 'A Moreninha', author: 'Joaquim Macedo', category: 'Literatura Brasileira',
    cover: 'linear-gradient(135deg,#1A1610,#854F0B)',
    isbn: 'xxx', editora: 'Ática', ano: '1844', paginas: '192', idioma: 'Português', edicao: '1',
    avail: 4, total: 4, digital: false, price: null, sinopse: '...', exemplares: []
  }
];
