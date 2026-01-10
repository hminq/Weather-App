# WeatherChartView

Custom View hiển thị biểu đồ nhiệt độ 24 giờ với đường cong smooth và tooltip tương tác.

## Cách sử dụng

### 1. Thêm vào Layout XML

```xml
<hminq.dev.weatherapp.presentation.widget.WeatherChartView
    android:id="@+id/weatherChart"
    android:layout_width="match_parent"
    android:layout_height="200dp" />
```

### 2. Khởi tạo trong Fragment/Activity

```kotlin
// Set giờ hiện tại cho indicator
val calendar = Calendar.getInstance()
binding.weatherChart.setSelectedHour(
    hour = calendar.get(Calendar.HOUR_OF_DAY),
    minute = calendar.get(Calendar.MINUTE)
)
```

### 3. Layout yêu cầu

Parent layout cần có các attribute sau để tooltip không bị clip:

```xml
<ConstraintLayout
    android:clipChildren="false"
    android:clipToPadding="false">
```

Nếu muốn chart tràn ra ngoài padding của parent, dùng negative margin:

```xml
<WeatherChartView
    android:layout_marginStart="@dimen/screen_padding_negative"
    android:layout_marginEnd="@dimen/screen_padding_negative" />
```

## Cấu trúc Component

```
WeatherChartView (FrameLayout)
├── chartView (Custom View)
│   ├── Area fill (gradient)
│   ├── Line (smooth curve)
│   └── Indicator (circle)
└── tooltipView (item_hourly_temp.xml)
    ├── Time text
    ├── Weather icon
    ├── Temperature
    └── Triangle pointer
```

## Tham số có thể điều chỉnh

Trong `WeatherChartView.kt`:

| Tham số | Mô tả | Default |
|---------|-------|---------|
| `chartPaddingTop` | Khoảng cách từ top view đến max point | 60dp |
| `chartPaddingBottom` | Khoảng cách từ min point đến bottom | 20dp |
| `chartPaddingHorizontal` | Padding trái/phải cho data points | 50dp |
| `minValueBuffer` | % từ bottom cho min value (0-1) | 0.5 |
| `maxValueBuffer` | % từ top cho max value (0-1) | 0.1 |
| `indicatorRadius` | Bán kính indicator circle | 8dp |
| `indicatorStrokeWidth` | Độ dày viền indicator | 3dp |
| `tooltipMargin` | Khoảng cách tooltip đến indicator | 12dp |

### Ví dụ điều chỉnh

```kotlin
// Chart cao hơn (gần top hơn)
chartPaddingTop = 40f.dpToPx()

// Nén chiều cao chart (ít dramatic hơn)
minValueBuffer = 0.6f  // Min ở 60% từ bottom
maxValueBuffer = 0.05f // Max ở 95% từ bottom

// Chart rộng hơn (tooltip có thể bị cắt ở edge)
chartPaddingHorizontal = 20f.dpToPx()
```

## Smooth Curve Algorithm

Sử dụng **Catmull-Rom Spline** để tạo đường cong mượt:

```kotlin
val tension = 0.3f  // Độ cong (nhỏ hơn = mượt hơn)

// Control points dựa trên 4 điểm liên tiếp
val cp1x = p1.x + (p2.x - p0.x) * tension
val cp1y = p1.y + (p2.y - p0.y) * tension
val cp2x = p2.x - (p3.x - p1.x) * tension
val cp2y = p2.y - (p3.y - p1.y) * tension
```

## Data Interpolation

- Input: 24 data points (1 per hour) từ API
- Output: 1440 points (60 per hour) với temperature interpolated
- Condition/Icon: Giữ nguyên theo giờ

```kotlin
// Linear interpolation giữa các giờ
val t = minute / 60f
val interpolatedTemp = currentTemp + (nextTemp - currentTemp) * t
```

## Theme Support

View tự động sử dụng Material theme colors:

| Element | Theme Attribute |
|---------|----------------|
| Line | `colorOnSurfaceVariant` |
| Indicator fill | `colorSurface` |
| Indicator stroke | `colorOnSurfaceVariant` |
| Gradient | `colorOnSurfaceVariant` với alpha |

## Dependencies

- `item_hourly_temp.xml` - Layout cho tooltip
- `bg_tooltip_shadow.xml` - Shadow drawable
- `bg_tooltip_content.xml` - Background drawable
- `ic_triangle_down_filled.xml` - Triangle icon

## Public Methods

```kotlin
/**
 * Set selected hour and minute for indicator position
 * @param hour 0-23
 * @param minute 0-59 (default: 0)
 */
fun setSelectedHour(hour: Int, minute: Int = 0)
```

## Touch Interaction

- **Drag**: Di chuyển indicator theo trục X
- **Touch bounds**: Tự động clamp trong view bounds
- **Snap**: Snap đến điểm dữ liệu gần nhất (mỗi phút)
