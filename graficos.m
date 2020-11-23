tamanhoDoArquivo = [50, 100, 150, 200];

tempoDeResposta2 = [84.957784782, 157.855352618, 235.610801559, 310.771303306];
tempoDeResposta3 = [53.629465838, 107.277532968, 155.136252361, 206.92171052];
tempoDeResposta4 = [41.771415792, 81.631810439, 116.209738053, 157.632811493];
tempoDeResposta5 = [33.259863053, 66.563453374, 95.230252173, 127.042588211];
tempoDeResposta6 = [23.769999111, 52.781828282, 78.780630861, 103.170493429];
tempoDeResposta7 = [24.419383026, 44.372432075, 69.786462304, 92.437961267];
tempoDeResposta8 = [37.213903426, 74.683059492, 111.962949976, 147.880599745];

tempoDeRespostaSeq = [164.916114151, 319.682700881, 474.739304538, 0];

qtdSlaves = [2, 3, 4, 5, 6, 7, 8];
overhead = [3.509877913, 3.442766003, 3.749514742, 3.601157822, 3.825621954, 3.928636487, 4.255365332];

hold on;

grid on;

% ========== PLOT DO GRAFICO Tempo de Resposta x Tamanho do arquivo ==========

% plot(tamanhoDoArquivo, tempoDeRespostaSeq, "*-r", "color", "c",                "linewidth", 2);
% plot(tamanhoDoArquivo, tempoDeResposta2,   "*-r", "color", "k",                "linewidth", 2);
% plot(tamanhoDoArquivo, tempoDeResposta3,   "*-r", "color", "r",                "linewidth", 2);
% plot(tamanhoDoArquivo, tempoDeResposta4,   "*-r", "color", [0.11, 0.74, 0],    "linewidth", 2);
% plot(tamanhoDoArquivo, tempoDeResposta5,   "*-r", "color", "b",                "linewidth", 2);
% plot(tamanhoDoArquivo, tempoDeResposta6,   "*-r", "color", [0.11, 0.74, 0.79], "linewidth", 2);
% plot(tamanhoDoArquivo, tempoDeResposta7,   "*-r", "color", "m",                "linewidth", 2);
% plot(tamanhoDoArquivo, tempoDeResposta8,   "*-r", "color", [0.91, 0.44, 0],    "linewidth", 2);


% xlabel("Tamanho do arquivo (Kbytes)", "fontsize", 15);
% ylabel("Tempo de resposta (s)", "fontsize", 15);
% title("Tempo de Resposta x Tamanho do arquivo", "fontsize", 15);

% h = legend("Sequencial", "2 slaves", "3 slaves", "4 slaves", "5 slaves", "6 slaves", "7 slaves", "8 slaves");
% legend (h, "location", "northeastoutside");
% set (h, "fontsize", 15);

% axis([0, 250]);

% ========== PLOT DO GRAFICO Overhead x Quantidade de slaves ==========

h = bar(qtdSlaves, overhead);

xlabel("Quantidade de slaves", "fontsize", 15);
ylabel("Overhead (s)", "fontsize", 15);
title("Overhead x Quantidade de slaves", "fontsize", 15);

set(gca, "xtick", 0:1:10, "ytick", 0:0.25:5);

hold off;

% epsfilename = "graficoTamArquivoXtempoResposta";
% epsfilename = "graficoOverhead";
% print(epsfilename, '-depsc2');
