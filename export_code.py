import os

# Имя выходного файла, в который соберется весь код
OUTPUT_FILE = 'android_project_code.txt'

# Расширения файлов, которые нужно скопировать (Kotlin, Java, XML, Gradle)
TARGET_EXTENSIONS = {'.kt', '.java', '.xml', '.gradle', '.properties'}

# Папки, которые нужно пропустить (чтобы не копировать тяжелые сборки и кэш)
IGNORE_DIRS = {'.git', '.gradle', '.idea', 'build', 'captures', '.settings'}

def collect_code(project_dir):
    code_content = []
    
    for root, dirs, files in os.walk(project_dir):
        # Удаляем папки из IGNORE_DIRS, чтобы os.walk не заходил в них
        dirs[:] = [d for d in dirs if d not in IGNORE_DIRS]
        
        for file in files:
            file_ext = os.path.splitext(file)[1].lower()
            
            if file_ext in TARGET_EXTENSIONS:
                file_path = os.path.join(root, file)
                
                # Относительный путь для читаемости в итоговом файле
                rel_file_path = os.path.relpath(file_path, project_dir)
                
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()
                        
                        code_content.append(f"\n{'='*80}\n")
                        code_content.append(f"FILE: {rel_file_path}\n")
                        code_content.append(f"{'='*80}\n\n")
                        code_content.append(content)
                        code_content.append("\n")
                except Exception as e:
                    print(f"Ошибка чтения файла {rel_file_path}: {e}")
                    
    return code_content

if __name__ == '__main__':
    project_root = os.getcwd() # Берет текущую папку, где лежит скрипт
    print(f"Сбор кода в проекте: {project_root}")
    
    all_code = collect_code(project_root)
    
    output_path = os.path.join(project_root, OUTPUT_FILE)
    with open(output_path, 'w', encoding='utf-8') as out_f:
        out_f.writelines(all_code)
        
    print(f"Готово! Весь код сохранен в: {output_path}")
