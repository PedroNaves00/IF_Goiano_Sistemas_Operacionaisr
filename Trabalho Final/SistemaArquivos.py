import os
import tkinter as tk
from tkinter import scrolledtext, messagebox

class SistemaDeArquivos:
    def __init__(self, espaco_total):
        self.espaco_total = espaco_total  # Total de espaço disponível em MB
        self.espaco_usado = 0  # Espaço ocupado
        self.diretorio_raiz = Diretorio("raiz")  # Diretório raiz
        self.diretorio_atual = self.diretorio_raiz

    def criar_diretorio(self, nome):
        novo_diretorio = Diretorio(nome)
        self.diretorio_atual.adicionar_diretorio(novo_diretorio)

    def criar_arquivo(self, nome, tamanho):
        if self.espaco_usado + tamanho <= self.espaco_total:
            novo_arquivo = Arquivo(nome, tamanho)
            self.diretorio_atual.adicionar_arquivo(novo_arquivo)
            self.espaco_usado += tamanho
        else:
            print("Espaço insuficiente para criar o arquivo!")

    def excluir_arquivo(self, nome):
        arquivo = self.diretorio_atual.obter_arquivo(nome)
        if arquivo:
            self.diretorio_atual.remover_arquivo(arquivo)
            self.espaco_usado -= arquivo.tamanho
        else:
            print("Arquivo não encontrado!")
    
    def mudar_diretorio(self, nome_diretorio):
            if nome_diretorio == "..":
                if self.diretorio_atual.diretorio_pai:
                    self.diretorio_atual = self.diretorio_atual.diretorio_pai
                else:
                    print("Você já está no diretório raiz.")
            else:
                diretorio = self.diretorio_atual.obter_diretorio(nome_diretorio)
                if diretorio:
                    self.diretorio_atual = diretorio
                else:
                    print("Diretório não encontrado.")

    def excluir_diretorio(self, nome):
        diretorio = self.diretorio_atual.obter_diretorio(nome)
        if diretorio:
            if not diretorio.arquivos and not diretorio.diretorios:
                self.diretorio_atual.remover_diretorio(diretorio)
            else:
                print("O diretório não está vazio!")
        else:
            print("Diretório não encontrado!")

    def listar_estrutura(self):
        self._listar_diretorio(self.diretorio_raiz, "")

    def _listar_diretorio(self, diretorio, indentacao):
        print(indentacao + f"[Diretório] {diretorio.nome}")
        for arquivo in diretorio.arquivos:
            print(indentacao + "  " + f"[Arquivo] {arquivo.nome} - {arquivo.tamanho}MB")
        for sub_diretorio in diretorio.diretorios:
            self._listar_diretorio(sub_diretorio, indentacao + "  ")

    def escrever_no_arquivo(self, nome, dados):
        arquivo = self.diretorio_atual.obter_arquivo(nome)
        if arquivo:
            arquivo.escrever(dados)
        else:
            print("Arquivo não encontrado!")

    def ler_do_arquivo(self, nome):
        arquivo = self.diretorio_atual.obter_arquivo(nome)
        if arquivo:
            return arquivo.ler()
        else:
            print("Arquivo não encontrado!")
            return None

class Diretorio:
    def __init__(self, nome, diretorio_pai=None):
        self.nome = nome
        self.arquivos = []  # Lista de arquivos no diretório
        self.diretorios = []  # Lista de subdiretórios
        self.diretorio_pai = diretorio_pai  # Lista de subdiretórios

    def adicionar_arquivo(self, arquivo):
        self.arquivos.append(arquivo)

    def remover_arquivo(self, arquivo):
        self.arquivos.remove(arquivo)

    def adicionar_diretorio(self, diretorio):
        self.diretorios.append(diretorio)

    def remover_diretorio(self, diretorio):
        self.diretorios.remove(diretorio)

    def obter_arquivo(self, nome):
        for arquivo in self.arquivos:
            if arquivo.nome == nome:
                return arquivo
        return None

    def obter_diretorio(self, nome):
        for diretorio in self.diretorios:
            if diretorio.nome == nome:
                return diretorio
        return None

class Arquivo:
    def __init__(self, nome, tamanho):
        self.nome = nome
        self.tamanho = tamanho  # Tamanho do arquivo em MB
        self.dados = ""  # Dados do arquivo

    def escrever(self, dados):
        self.dados = dados

    def ler(self):
        return self.dados


def executar_comando():
    comando = entrada_comando.get()
    saida_console.delete(1.0, tk.END)  # Limpa o console

    if comando.startswith("mkdir"):
        try:
            _, nome = comando.split()
            sistema.criar_diretorio(nome)
            saida_console.insert(tk.END, f"Diretório '{nome}' criado.\n")
        except ValueError:
            saida_console.insert(tk.END, "Erro: Sintaxe incorreta para mkdir.\n")

    elif comando.startswith("touch"):
        try:
            _, nome, tamanho = comando.split()
            sistema.criar_arquivo(nome, int(tamanho))
            saida_console.insert(tk.END, f"Arquivo '{nome}' criado com tamanho {tamanho}MB.\n")
        except ValueError:
            saida_console.insert(tk.END, "Erro: Sintaxe incorreta para touch.\n")

    elif comando.startswith("rm "):
        try:
            _, nome = comando.split()
            sistema.excluir_arquivo(nome)
            saida_console.insert(tk.END, f"Arquivo '{nome}' removido.\n")
        except ValueError:
            saida_console.insert(tk.END, "Erro: Sintaxe incorreta para rm.\n")

    elif comando.startswith("rmdir"):
        try:
            _, nome = comando.split()
            sistema.excluir_diretorio(nome)
            saida_console.insert(tk.END, f"Diretório '{nome}' removido.\n")
        except ValueError:
            saida_console.insert(tk.END, "Erro: Sintaxe incorreta para rmdir.\n")

    elif comando == "ls -lR":
        saida_console.insert(tk.END, "Estrutura de arquivos:\n")
        sistema.listar_estrutura()
        # Captura a saída do listar_estrutura e insere no console
        saida_console.insert(tk.END, captura_saida_ls.getvalue())
        captura_saida_ls.truncate(0) # Limpa o buffer

    elif comando.startswith("echo"):
        partes = comando.split(" > ")
        if len(partes) == 2:
            dados = partes[0][5:].strip()  # Remove "echo "
            nome = partes[1].strip()
            sistema.escrever_no_arquivo(nome, dados)
            saida_console.insert(tk.END, f"Dados escritos em '{nome}'.\n")
        else:
            saida_console.insert(tk.END, "Erro: Sintaxe incorreta para echo.\n")

    elif comando.startswith("cat"):
        try:
            _, nome = comando.split()
            dados = sistema.ler_do_arquivo(nome)
            if dados:
                saida_console.insert(tk.END, f"Conteúdo de '{nome}':\n{dados}\n")
            else:
                saida_console.insert(tk.END, f"Arquivo '{nome}' não encontrado.\n")
        except ValueError:
            saida_console.insert(tk.END, "Erro: Sintaxe incorreta para cat.\n")

    elif comando == "exit":
        root.destroy()  # Fecha a janela
    else:
        saida_console.insert(tk.END, "Comando inválido.\n")

# Configuração da GUI
root = tk.Tk()
root.title("Simulador de Sistema de Arquivos (macOS)")

# Entrada de comando
entrada_comando = tk.Entry(root, width=50)
entrada_comando.pack(pady=10)

# Botão de execução
botao_executar = tk.Button(root, text="Executar", command=executar_comando)
botao_executar.pack()

# Console de saída
saida_console = scrolledtext.ScrolledText(root, wrap=tk.WORD, width=60, height=20)
saida_console.pack(pady=10)

# Redireciona a saída do ls -lR para o console
import io
captura_saida_ls = io.StringIO()
import sys
sys.stdout = captura_saida_ls

# Sistema de arquivos
sistema = SistemaDeArquivos(100)

root.mainloop()