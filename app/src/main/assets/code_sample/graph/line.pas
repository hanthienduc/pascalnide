Program draw_line;
Uses Crt,Graph;
Var
    graphicsDriver, graphicsMode,
    errCode, i, x, y, maxColor,
    color: Integer;
Begin
    Writeln('Initialising Graphics, please wait...');
    graphicsDriver := Detect;
    InitGraph(graphicsDriver, graphicsMode,'');
    If GraphResult <> grOK then exit;

    Randomize;
    x := getMaxX();
    y := getMaxY();
    maxColor := getMaxColor();

    While (not keypressed) do
    Begin
        delay(50);
        color := random(maxColor) + 1;
        setColor(color);
        line(random(x), random(y), random(x), random(y));
    end;

    ReadLn;
    CloseGraph;
End.