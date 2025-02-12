import numpy as np
import random

# Criando a MATRIZ SWAP (100x6)
swap = np.zeros((100, 6), dtype=int)
for i in range(100):
    swap[i][0] = i  # Número da página (N)
    swap[i][1] = i + 1  # Instrução (I)
    swap[i][2] = random.randint(1, 50)  # Dado (D)
    swap[i][3] = 0  # Bit de Acesso (R)
    swap[i][4] = 0  # Bit de Modificação (M)
    swap[i][5] = random.randint(100, 9999)  # Tempo de Envelhecimento (T)

# Criando a MATRIZ RAM (10x6)
ram = np.zeros((10, 6), dtype=int)
indices_ram = random.sample(range(100), 10)
for i, idx in enumerate(indices_ram):
    ram[i] = swap[idx]

# Implementação do Algoritmo FIFO
fifo_queue = list(indices_ram)  # Fila FIFO

def fifo_replacement(page_number):
    global fifo_queue
    removed_page = fifo_queue.pop(0)  # Remove a página mais antiga
    new_page = swap[page_number].copy()
    index = np.where(ram[:, 0] == removed_page)[0][0]
    ram[index] = new_page  # Substitui a página na RAM
    fifo_queue.append(page_number)

def nru_replacement(page_number):
    classes = {0: [], 1: [], 2: [], 3: []}
    for i in range(10):
        r, m = ram[i][3], ram[i][4]
        classes[r * 2 + m].append(i)
    
    for priority in range(4):
        if classes[priority]:
            victim_index = random.choice(classes[priority])
            removed_page = ram[victim_index][0]
            new_page = swap[page_number].copy()
            ram[victim_index] = new_page
            return

def execute_simulation(algorithm):
    for i in range(1000):
        instruction = random.randint(1, 100)
        found = np.where(ram[:, 1] == instruction)[0]
        if found.size > 0:
            ram[found[0]][3] = 1  # Bit R = 1
            if random.random() < 0.5:
                ram[found[0]][2] += 1  # D = D + 1
                ram[found[0]][4] = 1  # Bit M = 1
        else:
            if algorithm == "FIFO":
                fifo_replacement(instruction - 1)
            elif algorithm == "NRU":
                nru_replacement(instruction - 1)
        if i % 10 == 0:
            ram[:, 3] = 0  # Zerar R a cada 10 instruções

# Executando as simulações
print("Matriz RAM Inicial:")
print(ram)
print("\nMatriz SWAP Inicial:")
print(swap)

execute_simulation("FIFO")
print("\nMatriz RAM após FIFO:")
print(ram)

execute_simulation("NRU")
print("\nMatriz RAM após NRU:")
print(ram)
