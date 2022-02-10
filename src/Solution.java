import java.io.IOException;

public class Solution {
    public static void externalSortTwoStageSimpleMerge(int count) throws IOException {
        FileWrapper fileWithData = new FileWrapper("files1//data.txt");
        FileWrapper fileA = new FileWrapper("files1//a.txt");
        FileWrapper fileB = new FileWrapper("files1//b.txt");
        FileWrapper fileC = new FileWrapper("files1//c.txt");
        
        //заполняем файл числами
        fileWithData.open();
        fileWithData.fillByNumbers(count);

        //копируем его в файл А
        fileA.open();
        fileA.copyFrom(fileWithData);
        fileWithData.close();
        
        //открываем файлы B и C
        fileB.open();
        fileC.open();

        //длина цепочки
        int len = 1;
        
        //выходим из цикла как только длина цепочки станет не меньше кол-во цифр
        while(len < fileA.getCount()) {
            fileB.clear();
            fileC.clear();

            //Переменная для определения файла для записи
            boolean diraction = true;

            //Разбиваем цепочки файла А по двум файлам B и C
            //цепочки под четными номерами   --> B
            //цепочки под нечетными номерами --> С
            for(int i = 0, len1 = Math.min(fileA.getCount() - i, len); i < fileA.getCount(); i += len1) {

                //цепочка может быть заполнена не до конца
                //чтобы не выйти за границы берем минимум
                //между кол-вом оставшихся чисел или len
                len1 = Math.min(fileA.getCount() - i, len);

                for(int j = i; j < i + len1; j++) {
                    int num = fileA.getNumberByPosition(j);
                    if(diraction) {
                        fileB.writeNumber(num);
                    } else {
                        fileC.writeNumber(num);
                    }
                }

                //меняем направление
                diraction = !diraction;
            }

            //объединяем файлы B и C в A
            mergeIntoFile(fileA, fileB, fileC, len);

            //удваиваем длину цепочки
            len *= 2;
        }

        //закрываем файлы
        fileA.close();
        fileB.close();
        fileC.close();
    }
    
    //объединяем 2 файла (B и C) в один (A)
    private static void mergeIntoFile(FileWrapper fileA, FileWrapper fileB, FileWrapper fileC, int len) throws IOException {
        fileA.clear();

        int countB = fileB.getCount();
        int countC = fileC.getCount();
        int minLen = Math.min(countB, countC);
        for(int i = 0; i < minLen; i += len) {
            int ib = i, ic = i;
            int borderB = Math.min(i + len, countB);
            int borderC = Math.min(i + len, countC);

            writeInOneFile(ib, borderB, ic, borderC, fileB, fileC, fileA);
        }

        if (countB + countC > fileA.getCount()) {
            fileA.copyFrom(fileB, minLen);
            fileA.copyFrom(fileC, minLen);
        }
    }

    //разбиваем файл на два (A --> B и C)
    private static void makeTwoFiles(FileWrapper readFile, FileWrapper writeFile1, FileWrapper writeFile2) throws IOException {
        boolean diraction = true;
        for(int i = 0; i < readFile.getCount(); i++) {
            int num = readFile.getNumberByPosition(i);

            if(diraction) {
                writeFile1.writeNumber(num);
            } else {
                writeFile2.writeNumber(num);
            }

            diraction = !diraction;
        }
    }

    // Пишем в файл упорядоченные цепочки из двух других файлов
    private static void writeInOneFile(FileWrapper readFile1, FileWrapper readFile2, FileWrapper writeFile) throws IOException {
        writeInOneFile(0, readFile1.getCount(), 0, readFile2.getCount(), readFile1, readFile2, writeFile);
    }

    private static void writeInOneFile(int i1, int j1, int i2, int j2, FileWrapper readFile1, FileWrapper readFile2, FileWrapper writeFile) throws IOException {
        while(i1 < j1 && i2 < j2) {
            int elem1 = readFile1.getNumberByPosition(i1);
            int elem2 = readFile2.getNumberByPosition(i2);

            if(elem1 < elem2) {
                writeFile.writeNumber(elem1);
                i1++;
            } else {
                writeFile.writeNumber(elem2);
                i2++;
            }
        }

        while(i1 < j1) {
            int elem1 = readFile1.getNumberByPosition(i1);
            writeFile.writeNumber(elem1);
            i1++;
        }

        while(i2 < j2) {
            int elem2 = readFile2.getNumberByPosition(i2);
            writeFile.writeNumber(elem2);
            i2++;
        }
    }

    public static void externalSortOneStageSimpleMerge(int count) throws IOException {
        FileWrapper fileWithData = new FileWrapper("files2//data.txt");
        FileWrapper fileA = new FileWrapper("files2//a.txt");
        FileWrapper fileB = new FileWrapper("files2//b.txt");
        FileWrapper fileC = new FileWrapper("files2//c.txt");
        FileWrapper fileD = new FileWrapper("files2//d.txt");
        FileWrapper fileE = new FileWrapper("files2//e.txt");

        //заполняем файл числами
        fileWithData.open();
        fileWithData.fillByNumbers(count);
        
        //копируем его в файл А
        fileA.open();
        fileA.copyFrom(fileWithData);
        fileWithData.close();

        //открываем файлы B и C
        fileB.open();
        fileC.open();

        //разбиваем файл A на 2 файла B и C
        makeTwoFiles(fileA, fileB, fileC);

        //открываем файлы D и E
        fileD.open();
        fileE.open();

        //true  --> читаем из B и C; пишем в D и E
        //false --> читаем из D и E; пишем в B и C
        boolean flag = true;

        //файлы для чтения
        FileWrapper from1 = fileB;
        FileWrapper from2 = fileC;

        //файлы для записи
        FileWrapper in1 = fileD;
        FileWrapper in2 = fileE;

        //длина цепочки
        int len = 1;

        //пока цепочек больше одной манипулируем файлами B, C, D, E
        while((from1.getCount() / (double)len > 1) || (from2.getCount() / (double)len > 1)) {
            //кол-во чисел в файлах
            int count1 = from1.getCount();
            int count2 = from2.getCount();

            //минимальное кол-во
            int minLen = Math.min(count1, count2);

            //Переменная для определения файла записи
            boolean diraction = true;

            for(int i = 0; i < minLen; i += len) {
                //начальные индексы текующих цепочек
                int i1 = i, i2 = i;

                //конечные индексы текующих цепочек
                int border1 = Math.min(i + len, count1);
                int border2 = Math.min(i + len, count2);

                //пишем в два других файла упорядоченные цепочки в зависимости от diraction
                //true  --> пишем из from1 и from2 в in1
                //false --> пишем из from1 и from2 в in2
                if(diraction){
                    writeInOneFile(i1, border1, i2, border2, from1, from2, in1);
                } else {
                    writeInOneFile(i1, border1, i2, border2, from1, from2, in2);
                }

                //меняем направление
                diraction = !diraction;
            }

            //нерасмотренные числа в одном из файлов копируем в in1 или in2
            //так как кол-во чисел в одном из них может быть больше minLen,
            //а итерировались мы до minLen
            if(from1.getCount() + from2.getCount() > in1.getCount() + in2.getCount()) {
                if(diraction) {
                    in1.copyFrom(from1, minLen);
                    in1.copyFrom(from2, minLen);
                    
                } else {
                    in2.copyFrom(from1, minLen);
                    in2.copyFrom(from2, minLen);
                }
            }

            //очищаем прочитанные файлы
            from1.clear();
            from2.clear();

            // запись <-> чтение
            if(flag) {
                from1 = fileD;
                from2 = fileE;
                in1 = fileB;
                in2 = fileC;

            } else {
                from1 = fileB;
                from2 = fileC;
                in1 = fileD;
                in2 = fileE;
            }
            flag = !flag;

            //удвоение длины цепочки
            len *= 2;
        }

        //очищаем файл A
        //Объединяем файлы ((B и C) или (D и E)) в один (A)
        fileA.clear();
        if(flag) {
            writeInOneFile(fileB, fileC, fileA);
        } else {
            writeInOneFile(fileD, fileE, fileA);
        }

        //закрываем все файлы
        fileA.close();
        fileB.close();
        fileC.close();
        fileD.close();
    }
}
