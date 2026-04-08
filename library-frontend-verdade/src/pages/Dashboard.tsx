import { useNavigate } from 'react-router-dom';

export default function Dashboard() {
  const navigate = useNavigate();

  const handleLogout = () => {
    navigate('/');
  };
  return (
    <div className="dashboard-page">
      {/* SIDEBAR */}
      <nav className="sidebar">
        <div className="sb-brand">
          <div className="sb-logo">L</div>
          <div className="sb-name">LLAM</div>
        </div>
        <div className="sb-nav">
          <div className="sb-section">Principal</div>
          <div className="sb-item active"><div className="sb-dot"></div>Dashboard</div>
          <div className="sb-item"><div className="sb-dot"></div>Catálogo</div>
          <div className="sb-item"><div className="sb-dot"></div>Minha Biblioteca</div>
          <div className="sb-section">Operação</div>
          <div className="sb-item"><div className="sb-dot"></div>Empréstimos</div>
          <div className="sb-item"><div className="sb-dot"></div>Devoluções</div>
          <div className="sb-item"><div className="sb-dot"></div>Reservas</div>
          <div className="sb-section">Gestão</div>
          <div className="sb-item"><div className="sb-dot"></div>Acervo</div>
          <div className="sb-item"><div className="sb-dot"></div>Leitores</div>
          <div className="sb-item"><div className="sb-dot"></div>Multas</div>
          <div className="sb-item"><div className="sb-dot"></div>Relatórios</div>
          <div className="sb-section">Config</div>
          <div className="sb-item"><div className="sb-dot"></div>Planos</div>
          <div className="sb-item"><div className="sb-dot"></div>Configurações</div>
        </div>
        <div className="sb-footer">
          <div className="sb-tenant">Instituto LLAM · Admin</div>
          <div className="sb-user">
            <div className="sb-avatar">MB</div>
            <div className="sb-uname">Maria Bibliotecária</div>
          </div>
          <button className="sb-logout" onClick={handleLogout}>Sair da conta</button>
        </div>
      </nav>

      {/* MAIN */}
      <div className="main-content">
        <div className="topbar">
          <div className="topbar-left">
            <div className="page-title">Dashboard</div>
            <div className="page-sub">Abril 2026 · Instituto LLAM</div>
          </div>
          <div className="topbar-right">
            <div className="badge-live"><div className="live-dot"></div>Atualiza em 52s</div>
            <button className="btn-ghost">Exportar</button>
            <button className="btn-gold">+ Novo Empréstimo</button>
          </div>
        </div>

        <div className="content-scroll">

          {/* KPI ROW 1 */}
          <div className="kpi-grid">
            <div className="kpi">
              <div className="kpi-top" style={{background: 'var(--gold)'}}></div>
              <div className="kpi-label">Total de Títulos</div>
              <div className="kpi-value">1.847</div>
              <div className="kpi-delta up">↑ 23 cadastrados este mês</div>
            </div>
            <div className="kpi">
              <div className="kpi-top" style={{background: 'var(--blue)'}}></div>
              <div className="kpi-label">Empréstimos Ativos</div>
              <div className="kpi-value">342</div>
              <div className="kpi-delta up">↑ 12 em relação a ontem</div>
            </div>
            <div className="kpi">
              <div className="kpi-top" style={{background: 'var(--green)'}}></div>
              <div className="kpi-label">Leitores Ativos</div>
              <div className="kpi-value">2.103</div>
              <div className="kpi-delta up">↑ 47 novos este mês</div>
            </div>
            <div className="kpi">
              <div className="kpi-top" style={{background: 'var(--red)'}}></div>
              <div className="kpi-label">Devoluções Pendentes</div>
              <div className="kpi-value">38</div>
              <div className="kpi-delta down">↑ 5 com prazo vencido</div>
            </div>
          </div>

          {/* KPI ROW 2 */}
          <div className="kpi-grid">
            <div className="kpi">
              <div className="kpi-label">Exemplares Disponíveis</div>
              <div className="kpi-value">4.291</div>
              <div className="kpi-delta neutral">de 4.897 totais no acervo</div>
            </div>
            <div className="kpi">
              <div className="kpi-label">Empréstimos Hoje</div>
              <div className="kpi-value">67</div>
              <div className="kpi-delta up">↑ 18% em relação a ontem</div>
            </div>
            <div className="kpi">
              <div className="kpi-top" style={{background: 'var(--gold)'}}></div>
              <div className="kpi-label">Receita do Mês</div>
              <div className="kpi-value" style={{fontSize: '22px'}}>R$ 3.840</div>
              <div className="kpi-delta up">↑ 11% vs março</div>
            </div>
            <div className="kpi">
              <div className="kpi-top" style={{background: 'var(--red)'}}></div>
              <div className="kpi-label">Multas Pendentes</div>
              <div className="kpi-value" style={{fontSize: '22px'}}>R$ 920</div>
              <div className="kpi-delta down">28 leitores inadimplentes</div>
            </div>
          </div>

          {/* CHARTS ROW */}
          <div className="panels-row">
            {/* Bar chart */}
            <div className="panel">
              <div className="panel-header">
                <div>
                  <div className="panel-title">Empréstimos — últimos 7 dias</div>
                  <div className="panel-sub">Total diário de empréstimos realizados</div>
                </div>
                <div className="panel-badge">7 dias</div>
              </div>
              <div className="bars-wrap">
                <div className="bar-col">
                  <div className="bar-num">44</div>
                  <div className="bar-body" style={{height:'100%'}}><div className="bar-fill" style={{height:'66%',background:'#C9A84C80'}}></div></div>
                  <div className="bar-day">Sáb</div>
                </div>
                <div className="bar-col">
                  <div className="bar-num">58</div>
                  <div className="bar-body" style={{height:'100%'}}><div className="bar-fill" style={{height:'87%',background:'#C9A84C80'}}></div></div>
                  <div className="bar-day">Dom</div>
                </div>
                <div className="bar-col">
                  <div className="bar-num">51</div>
                  <div className="bar-body" style={{height:'100%'}}><div className="bar-fill" style={{height:'76%',background:'#C9A84C80'}}></div></div>
                  <div className="bar-day">Seg</div>
                </div>
                <div className="bar-col">
                  <div className="bar-num">39</div>
                  <div className="bar-body" style={{height:'100%'}}><div className="bar-fill" style={{height:'58%',background:'#C9A84C80'}}></div></div>
                  <div className="bar-day">Ter</div>
                </div>
                <div className="bar-col">
                  <div className="bar-num">62</div>
                  <div className="bar-body" style={{height:'100%'}}><div className="bar-fill" style={{height:'93%',background:'#C9A84C80'}}></div></div>
                  <div className="bar-day">Qua</div>
                </div>
                <div className="bar-col">
                  <div className="bar-num">55</div>
                  <div className="bar-body" style={{height:'100%'}}><div className="bar-fill" style={{height:'82%',background:'#C9A84C80'}}></div></div>
                  <div className="bar-day">Qui</div>
                </div>
                <div className="bar-col">
                  <div className="bar-num">67</div>
                  <div className="bar-body" style={{height:'100%'}}><div className="bar-fill" style={{height:'100%',background:'#E8E4DC'}}></div></div>
                  <div className="bar-day" style={{color:'var(--gold)'}}>Hoje</div>
                </div>
              </div>
            </div>

            {/* Top 5 */}
            <div className="panel">
              <div className="panel-header">
                <div className="panel-title">Top 5 Títulos</div>
              </div>
              <div className="top-item">
                <div className="top-rank gold">1</div>
                <div className="top-info">
                  <div className="top-name">Dom Casmurro</div>
                  <div className="top-author">Machado de Assis</div>
                  <div className="top-bar"><div className="top-bar-fill" style={{width:'100%'}}></div></div>
                </div>
                <div className="top-count">48</div>
              </div>
              <div className="top-item">
                <div className="top-rank">2</div>
                <div className="top-info">
                  <div className="top-name">Clean Code</div>
                  <div className="top-author">Robert C. Martin</div>
                  <div className="top-bar"><div className="top-bar-fill" style={{width:'79%'}}></div></div>
                </div>
                <div className="top-count">38</div>
              </div>
              <div className="top-item">
                <div className="top-rank">3</div>
                <div className="top-info">
                  <div className="top-name">O Alquimista</div>
                  <div className="top-author">Paulo Coelho</div>
                  <div className="top-bar"><div className="top-bar-fill" style={{width:'65%'}}></div></div>
                </div>
                <div className="top-count">31</div>
              </div>
              <div className="top-item">
                <div className="top-rank">4</div>
                <div className="top-info">
                  <div className="top-name">Design Patterns</div>
                  <div className="top-author">Gang of Four</div>
                  <div className="top-bar"><div className="top-bar-fill" style={{width:'52%'}}></div></div>
                </div>
                <div className="top-count">25</div>
              </div>
              <div className="top-item">
                <div className="top-rank">5</div>
                <div className="top-info">
                  <div className="top-name">Capitães da Areia</div>
                  <div className="top-author">Jorge Amado</div>
                  <div className="top-bar"><div className="top-bar-fill" style={{width:'42%'}}></div></div>
                </div>
                <div className="top-count">20</div>
              </div>
            </div>
          </div>

          {/* BOTTOM ROW */}
          <div className="panels-row3">
            {/* Categorias */}
            <div className="panel">
              <div className="panel-header"><div className="panel-title">Categorias Populares</div></div>
              <div className="legend-item"><div className="legend-dot" style={{background:'var(--gold)'}}></div><div className="legend-label">Tecnologia</div><div className="legend-pct">34%</div></div>
              <div className="legend-item"><div className="legend-dot" style={{background:'var(--blue)'}}></div><div className="legend-label">Literatura</div><div className="legend-pct">28%</div></div>
              <div className="legend-item"><div className="legend-dot" style={{background:'var(--green)'}}></div><div className="legend-label">Ciências</div><div className="legend-pct">19%</div></div>
              <div className="legend-item"><div className="legend-dot" style={{background:'var(--red)'}}></div><div className="legend-label">Direito</div><div className="legend-pct">12%</div></div>
              <div className="legend-item"><div className="legend-dot" style={{background:'var(--border2)'}}></div><div className="legend-label">Outros</div><div className="legend-pct">7%</div></div>
              <div style={{height:'4px',background:'var(--border)',borderRadius:'2px',marginTop:'14px',overflow:'hidden'}}>
                <div style={{height:'100%',display:'flex'}}>
                  <div style={{width:'34%',background:'var(--gold)'}}></div>
                  <div style={{width:'28%',background:'var(--blue)'}}></div>
                  <div style={{width:'19%',background:'var(--green)'}}></div>
                  <div style={{width:'12%',background:'var(--red)'}}></div>
                  <div style={{width:'7%',background:'var(--border2)'}}></div>
                </div>
              </div>
            </div>

            {/* Receita */}
            <div className="panel">
              <div className="panel-header"><div className="panel-title">Receita do Mês</div></div>
              <div className="receita-row">
                <div className="receita-label">Empréstimos tarifados</div>
                <div className="receita-val" style={{color:'var(--gold)'}}>R$ 2.140</div>
              </div>
              <div className="receita-row">
                <div className="receita-label">Vendas digitais</div>
                <div className="receita-val" style={{color:'var(--green)'}}>R$ 1.700</div>
              </div>
              <div className="receita-row">
                <div className="receita-label">Multas arrecadadas</div>
                <div className="receita-val" style={{color:'var(--text1)'}}>R$ 480</div>
              </div>
              <div className="receita-row">
                <div className="receita-label">Taxa de atraso</div>
                <div className="receita-val" style={{color:'var(--red)'}}>11,1%</div>
              </div>
            </div>

            {/* Alertas */}
            <div className="panel">
              <div className="panel-header">
                <div className="panel-title">Alertas Críticos</div>
                <div className="panel-badge" style={{color:'var(--red)',background:'#E24B4A18'}}>4 ativos</div>
              </div>
              <div className="alert-item">
                <div className="alert-dot red"></div>
                <div className="alert-text">João Ferreira — 3 livros com +15 dias de atraso · Multa R$ 45</div>
                <div className="alert-badge red">crítico</div>
              </div>
              <div className="alert-item">
                <div className="alert-dot red"></div>
                <div className="alert-text">Ana Santos — pendência R$ 120 · acesso bloqueado</div>
                <div className="alert-badge red">bloqueado</div>
              </div>
              <div className="alert-item warn">
                <div className="alert-dot amber"></div>
                <div className="alert-text">12 exemplares em EM_PROCESSAMENTO há +7 dias</div>
                <div className="alert-badge amber">atenção</div>
              </div>
              <div className="alert-item warn">
                <div className="alert-dot amber"></div>
                <div className="alert-text">"Java Efetivo" — 0 disponíveis · 5 reservas na fila</div>
                <div className="alert-badge amber">acervo</div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}
