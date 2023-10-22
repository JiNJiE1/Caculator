#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <QDebug>
#include <QString>
#include <QToolButton>
#include <QTextCursor>
#include <QVBoxLayout>

//命名规范
//类名首字母大写，单词和单词之间首字母大写
//函数名变量名称首字母小写，单词和单词之间首字母大写

QString a;
QString b; //定义两个变量，a保存用户输入的数字，b为符号
QString texT;
bool Add=false;
bool Sub=false;
bool Mul=false;
bool Div=false;

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{
    ui->setupUi(this);
    this->setWindowIcon(QIcon(":/1.png"));
    ui->plainTextEdit->setPlainText("");
    texT = ui->plainTextEdit->toPlainText();
    QToolButton *numButtons[11]; //定义空间大小为11的数组存放按钮对象(把小数点包含进数字了
    for(int k=0;k<11;k++)
    {
        QString numName = "num"+QString::number(k);
        numButtons[k] = MainWindow::findChild<QToolButton *>(numName);
        connect(numButtons[k],SIGNAL(clicked(bool)),this,SLOT(numOnClick()));
    }
    connect(ui->jia,SIGNAL(clicked(bool)),this,SLOT(fuHao()));
    connect(ui->jian,SIGNAL(clicked(bool)),this,SLOT(fuHao()));
    connect(ui->cheng,SIGNAL(clicked(bool)),this,SLOT(fuHao()));
    connect(ui->chu,SIGNAL(clicked(bool)),this,SLOT(fuHao()));
    connect(ui->AC,SIGNAL(clicked(bool)),this,SLOT(fuHao()));
    connect(ui->shanchu,SIGNAL(clicked(bool)),this,SLOT(fuHao()));
    connect(ui->zuoyi,SIGNAL(clicked(bool)),this,SLOT(fuHao()));
    connect(ui->youyi,SIGNAL(clicked(bool)),this,SLOT(fuHao()));
    connect(ui->deng,SIGNAL(clicked(bool)),this,SLOT(equalNum()));
}
MainWindow::~MainWindow()
{
    delete ui;
}


void MainWindow::numOnClick()
{
    QToolButton *numName = (QToolButton*)sender();//sender()  返回的就是你信号来源的对象
    ui->plainTextEdit->textCursor().insertText(numName->text());
    texT = ui->plainTextEdit->toPlainText();
     //text().toDouble()将字符串String转换成double类型,QString::number(3)将数字3转换成字符串"3"
    if(Add)
    {
        int i = texT.indexOf("+");
        texT = texT.mid(i+1);
        b = texT;
    }
    else if(Sub)
    {
        int i = texT.indexOf("-");
        texT = texT.mid(i+1);
        b = texT;
    }
    else if(Mul)
    {
        int i = texT.indexOf("×");
        texT = texT.mid(i+1);
        b = texT;
    }
    else if(Div)
    {
        int i = texT.indexOf("÷");
        texT = texT.mid(i+1);
        b = texT;
    }
    else a=texT;
    qDebug()<<a<<b;
}

void MainWindow::fuHao()
{
    QToolButton *fh = (QToolButton*)sender();
    QString f = fh->text();
    if(!(Add||Sub||Mul||Div))//只限定输入一个运算符
    {
        if(f == "+")
        {
            Add = true;
            ui->plainTextEdit->textCursor().insertText("+");
        }
        if(f == "-")
        {
            Sub = true;
            ui->plainTextEdit->textCursor().insertText("-");
        }
        if(f == "×")
        {
            Mul = true;
            ui->plainTextEdit->textCursor().insertText("×");
        }
        if(f == "÷")
        {
            Div = true;
            ui->plainTextEdit->textCursor().insertText("÷");
        }
    }

    if(f == "←")
    {
        texT = ui->plainTextEdit->toPlainText();
        texT.chop(1);
        Add=Sub=Mul=Div=false;
        matchFh(); //识别输入文本的符号与分割
        ui->plainTextEdit->setPlainText(texT);
        ui->plainTextEdit->moveCursor(QTextCursor::End);
    }
    if(f == "zuoyi")
        ui->plainTextEdit->moveCursor(QTextCursor::Left);
    if(f == "youyi")
        ui->plainTextEdit->moveCursor(QTextCursor::Right);
    if(f == "AC")
    {
        //a=b=0;
        Add=Sub=Mul=Div=false;
        ui->plainTextEdit->setPlainText("");
    }
}

void MainWindow::matchFh() //识别输入文本的符号与分割
{
    if(texT.contains("+",Qt::CaseSensitive)) //检测输入文本是否有“+”号
    {
        QStringList t = texT.split("+");
        a = t[0]; //将分割的字符分别赋值给a和b
        b = t[1];
        Add = true;
    }
    else if(texT.contains("-",Qt::CaseSensitive)) //检测输入文本是否有“-”号
    {
        QStringList t = texT.split("-");
        a = t[0];
        b = t[1];
        Sub = true;
    }
    else if(texT.contains("×",Qt::CaseSensitive)) //检测输入文本是否有“×”号
    {
        QStringList t = texT.split("×");
        a = t[0];
        b = t[1];
        Mul = true;
    }
    else if(texT.contains("÷",Qt::CaseSensitive)) //检测输入文本是否有“÷”号
    {
        QStringList t = texT.split("÷");
        a = t[0];
        b = t[1];
        Div = true;
    }
    else a = texT; //这是删掉了很多只剩数字的情况了
}
void MainWindow::equalNum()
{
    double x;
    double y;
    texT = ui->plainTextEdit->toPlainText();
    matchFh();
    x = a.toDouble();
    y = b.toDouble();
    qDebug()<<"x:"<<x<<"y:"<<y;
    if(Add)
    {
        ui->plainTextEdit->setPlainText(a=QString::number(x+y));
        Add = false;
    }
    if(Sub)
    {
        ui->plainTextEdit->setPlainText(a=QString::number(x-y));
        Sub = false;
    }
    if(Mul)
    {
        ui->plainTextEdit->setPlainText(a=QString::number(x*y));
        Mul = false;
    }
    if(Div)
    {
        ui->plainTextEdit->setPlainText(a=QString::number(x/y));
        Div = false;
    }
    ui->plainTextEdit->moveCursor(QTextCursor::End);
}

