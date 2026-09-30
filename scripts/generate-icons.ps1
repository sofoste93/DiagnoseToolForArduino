$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Drawing
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$resource = Join-Path $root "src\main\resources\com\sofoste\arduino"
New-Item -ItemType Directory -Path $resource -Force | Out-Null
$bitmap = New-Object System.Drawing.Bitmap 1024, 1024
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
$graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
$graphics.Clear([System.Drawing.Color]::FromArgb(5, 14, 12))
$lime = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(142, 255, 90)), 58
$cyan = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(85, 233, 255)), 24
$white = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(240, 255, 249)), 32
$graphics.DrawEllipse($lime, 152, 152, 720, 720)
$graphics.DrawArc($cyan, 95, 95, 834, 834, 202, 98)
$points = [System.Drawing.Point[]]@(
  (New-Object System.Drawing.Point 205, 520),
  (New-Object System.Drawing.Point 335, 520),
  (New-Object System.Drawing.Point 410, 370),
  (New-Object System.Drawing.Point 530, 670),
  (New-Object System.Drawing.Point 620, 455),
  (New-Object System.Drawing.Point 690, 520),
  (New-Object System.Drawing.Point 815, 520)
)
$graphics.DrawLines($white, $points)
$brush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(85, 233, 255))
$graphics.FillEllipse($brush, 802, 158, 70, 70)
$png = Join-Path $resource "icon.png"
$bitmap.Save($png, [System.Drawing.Imaging.ImageFormat]::Png)
$icon = [System.Drawing.Icon]::FromHandle($bitmap.GetHicon())
$stream = [System.IO.File]::Create((Join-Path $resource "app.ico"))
$icon.Save($stream)
$stream.Dispose()
$icon.Dispose()
$brush.Dispose(); $white.Dispose(); $cyan.Dispose(); $lime.Dispose(); $graphics.Dispose(); $bitmap.Dispose()
