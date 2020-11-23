tamanhoDoArquivo = [50, 100, 150, 200];

tempoDeResposta2 = [84.957784782, 157.855352618, 235.610801559, 310.771303306];
tempoDeResposta3 = [53.629465838, 107.277532968, 155.136252361, 206.92171052];
tempoDeResposta4 = [41.771415792, 81.631810439, 116.209738053, 157.632811493];
tempoDeResposta5 = [33.259863053, 66.563453374, 95.230252173, 127.042588211];
tempoDeResposta6 = [23.769999111, 52.781828282, 78.780630861, 103.170493429];
tempoDeResposta7 = [24.419383026, 44.372432075, 69.786462304, 92.437961267];
tempoDeResposta8 = [37.213903426, 74.683059492, 111.962949976, 147.880599745];

tempoDeRespostaSeq = [164.916114151, 319.682700881, 474.739304538, 623.256919687];

qtdSlaves = [2, 3, 4, 5, 6, 7, 8];

overhead = [3.509877913, 3.442766003, 3.749514742, 3.601157822, 3.825621954, 3.928636487, 4.255365332];

speedup2 =  [1.9411536514772776, 2.0251622487240706, 2.014930136465408, 2.005516317165591];
speedup3 =  [3.075102680477307, 2.979959475544229, 3.0601442107373327, 3.0120421782747595];
speedup4 =  [3.948061396151779, 3.9161535088075174, 4.085193827056772, 3.953852714951272];
speedup5 =  [4.95841230278682, 4.802676013289142, 4.985173237550238, 4.905889658449475];
speedup6 =  [6.937994123638063, 6.056681082985909, 6.026091684587126, 6.041038469161861];
speedup7 =  [6.753492255533614, 7.204534120209142, 6.8027420915816945, 6.742434722102643];
speedup8 =  [4.431572583589259, 4.280524968520394, 4.240146447014513, 4.21459556400043];

eficiencia2 =  [0.9705768257386388, 1.0125811243620353, 1.007465068232704, 1.0027581585827956];
eficiencia3 =  [1.025034226825769, 0.9933198251814096, 1.0200480702457775, 1.0040140594249198];
eficiencia4 =  [0.9870153490379447, 0.9790383772018794, 1.021298456764193, 0.988463178737818];
eficiencia5 =  [0.991682460557364, 0.9605352026578284, 0.9970346475100476, 0.981177931689895];
eficiencia6 =  [1.1563323539396773, 1.009446847164318, 1.0043486140978544, 1.0068397448603101];
eficiencia7 =  [0.9647846079333735, 1.0292191600298775, 0.971820298797385, 0.9632049603003775];
eficiencia8 =  [0.5539465729486573, 0.5350656210650493, 0.5300183058768141, 0.5268244455000537];

hold on;

grid on;

% ========== PLOT DO GRAFICO Tempo de Resposta x Tamanho do arquivo ==========

% plot(tamanhoDoArquivo, tempoDeRespostaSeq, "*-r", "color", [1, 0.83, 0],       "linewidth", 2);
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

% h = bar(qtdSlaves, overhead);

% xlabel("Quantidade de slaves", "fontsize", 15);
% ylabel("Overhead (s)", "fontsize", 15);
% title("Overhead x Quantidade de slaves", "fontsize", 15);

% set(gca, "xtick", 0:1:10, "ytick", 0:0.25:5);

% ========== PLOT DO GRAFICO Speed up x Tamanho do arquivo ==========

% plot(tamanhoDoArquivo, speedup2,   "*-r", "color", "k",                "linewidth", 2);
% plot(tamanhoDoArquivo, speedup3,   "*-r", "color", "r",                "linewidth", 2);
% plot(tamanhoDoArquivo, speedup4,   "*-r", "color", [0.11, 0.74, 0],    "linewidth", 2);
% plot(tamanhoDoArquivo, speedup5,   "*-r", "color", "b",                "linewidth", 2);
% plot(tamanhoDoArquivo, speedup6,   "*-r", "color", [0.11, 0.74, 0.79], "linewidth", 2);
% plot(tamanhoDoArquivo, speedup7,   "*-r", "color", "m",                "linewidth", 2);
% plot(tamanhoDoArquivo, speedup8,   "*-r", "color", [0.91, 0.44, 0],    "linewidth", 2);

% xlabel("Tamanho do arquivo (Kbytes)", "fontsize", 15);
% ylabel("Speed up", "fontsize", 15);
% title("Speed up x Tamanho do arquivo", "fontsize", 15);

% h = legend("2 slaves", "3 slaves", "4 slaves", "5 slaves", "6 slaves", "7 slaves", "8 slaves");
% legend (h, "location", "northeastoutside");
% set (h, "fontsize", 15);

% ========== PLOT DO GRAFICO Eficiencia x Tamanho do arquivo ==========

% plot(tamanhoDoArquivo, eficiencia2,   "*-r", "color", "k",                "linewidth", 2);
% plot(tamanhoDoArquivo, eficiencia3,   "*-r", "color", "r",                "linewidth", 2);
% plot(tamanhoDoArquivo, eficiencia4,   "*-r", "color", [0.11, 0.74, 0],    "linewidth", 2);
% plot(tamanhoDoArquivo, eficiencia5,   "*-r", "color", "b",                "linewidth", 2);
% plot(tamanhoDoArquivo, eficiencia6,   "*-r", "color", [0.11, 0.74, 0.79], "linewidth", 2);
% plot(tamanhoDoArquivo, eficiencia7,   "*-r", "color", "m",                "linewidth", 2);
% plot(tamanhoDoArquivo, eficiencia8,   "*-r", "color", [0.91, 0.44, 0],    "linewidth", 2);

% xlabel("Tamanho do arquivo (Kbytes)", "fontsize", 15);
% ylabel("Eficiencia", "fontsize", 15);
% title("Eficiencia x Tamanho do arquivo", "fontsize", 15);

% h = legend("2 slaves", "3 slaves", "4 slaves", "5 slaves", "6 slaves", "7 slaves", "8 slaves");
% legend (h, "location", "northeastoutside");
% set (h, "fontsize", 15);

hold off;

% epsfilename = "graficoTamArquivoXtempoResposta";
% epsfilename = "graficoOverhead";
% epsfilename = "graficoSpeedup";
% epsfilename = "graficoEficiencia";
% print(epsfilename, '-depsc2');
