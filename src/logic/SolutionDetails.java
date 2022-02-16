package logic;
import java.io.IOException;

import Utils.FileWrapper;

public class SolutionDetails {
    public static double[] externalSortTwoStageSimpleMerge(FileWrapper fileWithData) throws IOException {
        FileWrapper fileA = new FileWrapper("other//files1//a.txt");
        FileWrapper fileB = new FileWrapper("other//files1//b.txt");
        FileWrapper fileC = new FileWrapper("other//files1//c.txt");

        fileWithData.open();
        fileA.openAndClear();
        fileA.copyFrom(fileWithData);
        fileWithData.close();
        
        //открываем файлы B и C
        fileB.openAndClear();
        fileC.openAndClear();

        //длина цепочки
        int len = 1;
        
        double start = System.nanoTime();
        double countRead = 0;
        double countWrite = 0;
        double countCompare = 0;

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
                    countRead++;
                    if(diraction) {
                        fileB.writeNumber(num);
                        countWrite++;
                    } else {
                        fileC.writeNumber(num);
                        countWrite++;
                    }
                }

                //меняем направление
                diraction = !diraction;
            }

            //объединяем файлы B и C в A
            double[] c = mergeIntoFile(fileA, fileB, fileC, len);
            countRead += c[0];
            countWrite += c[1];
            countCompare += c[2];

            //удваиваем длину цепочки
            len *= 2;
        }

        double time = System.nanoTime() - start;


        //закрываем файлы
        fileA.close();
        fileB.close();
        fileC.close();

        double[] arr = new double[]{time, countRead, countWrite, countCompare};
        return arr;
    }
    
    //объединяем 2 файла (B и C) в один (A)
    private static double[] mergeIntoFile(FileWrapper fileA, FileWrapper fileB, FileWrapper fileC, int len) throws IOException {
        fileA.clear();

        double countRead = 0;
        double countWrite = 0;
        double countCompare = 0;

        int countB = fileB.getCount();
        int countC = fileC.getCount();
        int minLen = Math.min(countB, countC);
        for(int i = 0; i < minLen; i += len) {
            int ib = i, ic = i;
            int borderB = Math.min(i + len, countB);
            int borderC = Math.min(i + len, countC);

            double[] c = writeInOneFile(ib, borderB, ic, borderC, fileB, fileC, fileA);
            countRead += c[0];
            countWrite += c[1];
            countCompare += c[2];
        }

        if (countB + countC > fileA.getCount()) {
            fileA.copyFrom(fileB, minLen);
            fileA.copyFrom(fileC, minLen);

            
            countRead += fileB.getCount() - minLen;
            countWrite += fileB.getCount() - minLen;

            countRead += fileC.getCount() - minLen;
            countWrite += fileC.getCount() - minLen;
        }

        return new double[]{countRead, countWrite, countCompare};
    }

    //разбиваем файл на два (A --> B и C)
    private static double[] makeTwoFiles(FileWrapper readFile, FileWrapper writeFile1, FileWrapper writeFile2) throws IOException {
        boolean diraction = true;
        double countRead = 0;
        double countWrite = 0;
        double countCompare = 0;
        for(int i = 0; i < readFile.getCount(); i++) {
            int num = readFile.getNumberByPosition(i);
            countRead++;

            if(diraction) {
                writeFile1.writeNumber(num);
                countWrite++;
            } else {
                writeFile2.writeNumber(num);
                countWrite++;
            }

            diraction = !diraction;
        }

        return new double[]{countRead, countWrite, countCompare};
    }

    // Пишем в файл упорядоченные цепочки из двух других файлов
    private static double[] writeInOneFile(FileWrapper readFile1, FileWrapper readFile2, FileWrapper writeFile) throws IOException {
        return writeInOneFile(0, readFile1.getCount(), 0, readFile2.getCount(), readFile1, readFile2, writeFile);
    }

    private static double[] writeInOneFile(int i1, int j1, int i2, int j2, FileWrapper readFile1, FileWrapper readFile2, FileWrapper writeFile) throws IOException {
        double countRead = 0;
        double countWrite = 0;
        double countCompare = 0;
        boolean readedElem1 = false;
        boolean readedElem2 = false;
        int elem1 = -1;
        int elem2 = -1;
        while(i1 < j1 && i2 < j2) {
            if (!readedElem1) {
                elem1 = readFile1.getNumberByPosition(i1);
                countRead++;
                readedElem1 = true;
            }
            if (!readedElem2) {
                elem2 = readFile2.getNumberByPosition(i2);
                countRead++;
                readedElem2 = true;
            }

            countCompare++;
            if(elem1 < elem2) {
                writeFile.writeNumber(elem1);
                readedElem1 = false;
                countWrite++;
                i1++;
            } else {
                writeFile.writeNumber(elem2);
                readedElem2 = false;
                countWrite++;
                i2++;
            }
        }

        if(i1 < j1) {
            writeFile.writeNumber(elem1);
            countWrite++;
            i1++;
        }

        while(i1 < j1) {
            elem1 = readFile1.getNumberByPosition(i1);
            writeFile.writeNumber(elem1);
            countRead++;
            countWrite++;
            i1++;
        }

        if(i2 < j2) {
            writeFile.writeNumber(elem2);
            countWrite++;
            i2++;
        }

        while(i2 < j2) {
            elem2 = readFile2.getNumberByPosition(i2);
            writeFile.writeNumber(elem2);
            countRead++;
            countWrite++;
            i2++;
        }

        return new double[]{countRead, countWrite, countCompare};
    }

    public static double[] externalSortOneStageSimpleMerge(FileWrapper fileWithData) throws IOException {
        double countRead = 0;
        double countWrite = 0;
        double countCompare = 0;

        FileWrapper fileA = new FileWrapper("other//files2//a.txt");
        FileWrapper fileB = new FileWrapper("other//files2//b.txt");
        FileWrapper fileC = new FileWrapper("other//files2//c.txt");
        FileWrapper fileD = new FileWrapper("other//files2//d.txt");
        FileWrapper fileE = new FileWrapper("other//files2//e.txt");

        fileWithData.open();
        fileA.openAndClear();
        fileA.copyFrom(fileWithData);
        fileWithData.close();

        //открываем файлы B и C
        fileB.openAndClear();
        fileC.openAndClear();

        //открываем файлы D и E
        fileD.openAndClear();
        fileE.openAndClear();

        //разбиваем файл A на 2 файла B и C
        double start = System.nanoTime();
        double[] c = makeTwoFiles(fileA, fileB, fileC);
        countRead += c[0];
        countWrite += c[1];
        countCompare += c[2];

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
        while(2*len < fileA.getCount()) {
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
                    c = writeInOneFile(i1, border1, i2, border2, from1, from2, in1);
                } else {
                    c = writeInOneFile(i1, border1, i2, border2, from1, from2, in2);
                }

                countRead += c[0];
                countWrite += c[1];
                countCompare += c[2];

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

                countRead += from1.getCount() - minLen;
                countWrite += from1.getCount() - minLen;
                
                countRead += from2.getCount() - minLen;
                countWrite += from2.getCount() - minLen;
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
            c = writeInOneFile(fileB, fileC, fileA);
        } else {
            c = writeInOneFile(fileD, fileE, fileA);
        }

        countRead += c[0];
        countWrite += c[1];
        countCompare += c[2];

        double time = System.nanoTime() - start;

        //закрываем все файлы
        fileA.close();
        fileB.close();
        fileC.close();
        fileD.close();

        return new double[]{time, countRead, countWrite, countCompare};
    }
}
