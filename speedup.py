tamanhoDoArquivo = [50, 100, 150, 200]

tempoDeResposta2 = [84.957784782, 157.855352618, 235.610801559, 310.771303306]
tempoDeResposta3 = [53.629465838, 107.277532968, 155.136252361, 206.92171052]
tempoDeResposta4 = [41.771415792, 81.631810439, 116.209738053, 157.632811493]
tempoDeResposta5 = [33.259863053, 66.563453374, 95.230252173, 127.042588211]
tempoDeResposta6 = [23.769999111, 52.781828282, 78.780630861, 103.170493429]
tempoDeResposta7 = [24.419383026, 44.372432075, 69.786462304, 92.437961267]
tempoDeResposta8 = [37.213903426, 74.683059492, 111.962949976, 147.880599745]

tempoDeRespostaSeq = [164.916114151, 319.682700881, 474.739304538, 0]

for i, t in enumerate(tempoDeRespostaSeq):
    print("=" * 20)

    speedup2 = tempoDeRespostaSeq[i] / tempoDeResposta2[i]
    speedup3 = tempoDeRespostaSeq[i] / tempoDeResposta3[i]
    speedup4 = tempoDeRespostaSeq[i] / tempoDeResposta4[i]
    speedup5 = tempoDeRespostaSeq[i] / tempoDeResposta5[i] 
    speedup6 = tempoDeRespostaSeq[i] / tempoDeResposta6[i] 
    speedup7 = tempoDeRespostaSeq[i] / tempoDeResposta7[i] 
    speedup8 = tempoDeRespostaSeq[i] / tempoDeResposta8[i] 

    speedups = [speedup2, speedup3, speedup4, speedup5, speedup6, speedup7, speedup8]

    eficiencia = []

    for j, s in enumerate(speedups, start = 2):
        eficiencia.append(s / j) 

    print(tamanhoDoArquivo[i], "Kbytes: ")

    print("Speedup:", speedups, sep = "\n", end = "\n\n")
    print("Eficiencia:", eficiencia, sep = "\n")



