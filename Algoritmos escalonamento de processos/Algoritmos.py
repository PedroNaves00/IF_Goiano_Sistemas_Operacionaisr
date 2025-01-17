from collections import deque
import random

class Processo:
    def __init__(self, id, chegada, burst, prioridade):
        self.id = id
        self.chegada = chegada
        self.burst = burst
        self.prioridade = prioridade
        self.tempo_espera = 0
        self.tempo_retorno = 0

def fcfs(processos):
    processos.sort(key=lambda p: p.chegada)  # ordena por tempo de chegada
    tempo_atual = 0
    ordem_execucao = []

    for p in processos:
        if tempo_atual < p.chegada:
            tempo_atual = p.chegada
        p.tempo_espera = tempo_atual - p.chegada
        p.tempo_retorno = p.tempo_espera + p.burst
        tempo_atual += p.burst
        ordem_execucao.append(p.id)

    return ordem_execucao, processos

def priority_scheduling(processos):
    processos.sort(key=lambda p: (p.chegada, p.prioridade))  # ordena por chegada e prioridade
    tempo_atual = 0
    ordem_execucao = []
    fila = []
    index = 0

    while index < len(processos) or fila:
        while index < len(processos) and processos[index].chegada <= tempo_atual:
            fila.append(processos[index])
            index += 1
        
        if fila:
            fila.sort(key=lambda p: p.prioridade)  # Ordena a fila por prioridade
            p = fila.pop(0)
            p.tempo_espera = tempo_atual - p.chegada
            p.tempo_retorno = p.tempo_espera + p.burst
            tempo_atual += p.burst
            ordem_execucao.append(p.id)
        else:
            tempo_atual += 1

    return ordem_execucao, processos

def sjf(processes):
    # Ordenar processos pelo tempo de chegada e, em seguida, pelo tempo de execução
    processes.sort(key=lambda x: (x['arrival_time'], x['burst_time']))
    current_time = 0
    waiting_time = []
    turnaround_time = []

    for process in processes:
        if current_time < process['arrival_time']:
            current_time = process['arrival_time']
        waiting_time.append(current_time - process['arrival_time'])
        current_time += process['burst_time']
        turnaround_time.append(current_time - process['arrival_time'])

    avg_waiting_time = sum(waiting_time) / len(processes)
    avg_turnaround_time = sum(turnaround_time) / len(processes)

    print("Ordem de Execução: ", [p['id'] for p in processes])
    print("Tempo de Espera: ", waiting_time)
    print("Tempo de Retorno: ", turnaround_time)
    print(f"Tempo Médio de Espera: {avg_waiting_time:.2f}")
    print(f"Tempo Médio de Retorno: {avg_turnaround_time:.2f}")


def lottery_scheduling(processes):
    tickets = []
    for process in processes:
        tickets.extend([process['id']] * process['priority'])

    current_time = 0
    waiting_time = {process['id']: 0 for process in processes}
    turnaround_time = {process['id']: 0 for process in processes}

    completed = set()
    while len(completed) < len(processes):
        chosen_ticket = random.choice(tickets)
        for process in processes:
            if process['id'] == chosen_ticket and process['id'] not in completed:
                if current_time < process['arrival_time']:
                    current_time = process['arrival_time']
                waiting_time[process['id']] = current_time - process['arrival_time']
                current_time += process['burst_time']
                turnaround_time[process['id']] = current_time - process['arrival_time']
                completed.add(process['id'])
                tickets = [t for t in tickets if t != chosen_ticket]
                break

    avg_waiting_time = sum(waiting_time.values()) / len(processes)
    avg_turnaround_time = sum(turnaround_time.values()) / len(processes)

    print("Ordem de Execução: ", list(completed))
    print("Tempo de Espera: ", list(waiting_time.values()))
    print("Tempo de Retorno: ", list(turnaround_time.values()))
    print(f"Tempo Médio de Espera: {avg_waiting_time:.2f}")
    print(f"Tempo Médio de Retorno: {avg_turnaround_time:.2f}")

def round_robin_simulation(processes, burst_times, quantum):
 
    queue = deque()
    n = len(processes)
    remaining_times = burst_times.copy()
    time_elapsed = 0
    process_log = []  # Registra cada execução

    # Adiciona todos os processos na fila
    for i in range(n):
        queue.append(i)

    print("Iniciando a simulação de escalonamento...\n")

    while queue:
        process_index = queue.popleft()
        process_name = processes[process_index]

        print(f"[Tempo {time_elapsed}] Executando {process_name}")
        process_log.append((process_name, time_elapsed))

        if remaining_times[process_index] > quantum:
            # Executa o processo por um quantum
            time_elapsed += quantum
            remaining_times[process_index] -= quantum
            print(
                f"{process_name} executou por {quantum} unidades e "
                f"ainda precisa de {remaining_times[process_index]}."
            )
            # Reinsere na fila
            queue.append(process_index)
        else:
            # Finaliza o processo
            time_elapsed += remaining_times[process_index]
            print(
                f"{process_name} terminou após "
                f"{remaining_times[process_index]} unidades."
            )
            remaining_times[process_index] = 0

    print("\nSimulação concluída!")
    print(f"Tempo total: {time_elapsed} unidades.")
    print("Log de execução:", process_log)


# Configurações do exemplo
processes = ['P1', 'P2', 'P3', 'P4']
burst_times = [7, 5, 3, 10]
quantum = 4

round_robin_simulation(processes, burst_times, quantum)

# Exemplo de entrada
processes = [
    {'id': 'P1', 'arrival_time': 0, 'burst_time': 5, 'priority': 2},
    {'id': 'P2', 'arrival_time': 2, 'burst_time': 3, 'priority': 1},
    {'id': 'P3', 'arrival_time': 4, 'burst_time': 8, 'priority': 3},
    {'id': 'P4', 'arrival_time': 5, 'burst_time': 6, 'priority': 2},
    {'id': 'P5', 'arrival_time': 11, 'burst_time': 8, 'priority': 1}
]

print("\nSJF (Shortest Job First)")
sjf(processes)

print("\nLottery Scheduling")
lottery_scheduling(processes)

def escrever_resultados(arquivo, nome_algoritmo, ordem_execucao, processos):
    with open(arquivo, 'a') as f:
        f.write(f"{nome_algoritmo}\n")
        f.write("Ordem de Execução: " + " → ".join(ordem_execucao) + "\n")
        for p in processos:
            f.write(f"Processo: {p.id} Tempo de espera: {p.tempo_espera} Tempo de Retorno: {p.tempo_retorno}\n")
        f.write("-------\n")

# entrada de processos
processos = [
    Processo("P1", 0, 5, 2),
    Processo("P2", 2, 8, 1),
    Processo("P3", 4, 6, 3),
    Processo("P4", 5, 5, 1),
    Processo("P5", 6, 3, 2)
]

# Executa e grava os resultados do FCFS
ordem_fcfs, processos_fcfs = fcfs(processos.copy())
escrever_resultados("resultados_escalonamento.txt", "FCFS", ordem_fcfs, processos_fcfs)

# Executa e grava os resultados do Priority Scheduling
ordem_priority, processos_priority = priority_scheduling(processos.copy())
escrever_resultados("resultados_escalonamento.txt", "Priority Scheduling", ordem_priority, processos_priority)
