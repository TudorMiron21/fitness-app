import 'dart:math';

import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';

class PieChartWidget extends StatefulWidget {
  final String title;
  final Map<String, double> percentages;

  PieChartWidget({Key? key, required this.title, required this.percentages})
      : super(key: key);

  @override
  _PieChartWidgetState createState() => _PieChartWidgetState();
}

class _PieChartWidgetState extends State<PieChartWidget> {
  int? _touchedIndex;

  @override
  Widget build(BuildContext context) {

    return _buildPieChartSection(widget.title, widget.percentages);
  }

  Widget _buildPieChartSection(String title, Map<String, double> percentages) {
 List<PieChartSectionData> sections = percentages.entries
      .map<PieChartSectionData>((entry) { // Cast each entry to PieChartSectionData
        final isTouched = _touchedIndex == entry.key; // Assuming entry.key is indexable
        final fontSize = isTouched ? 18.0 : 16.0;
        final radius = isTouched ? 60.0 : 50.0;

        return PieChartSectionData(
          color: _getRandomColor(),
          value: entry.value * 100,
          title: isTouched ? '${entry.value * 100}% ${entry.key}' : '',
          radius: radius,
          titleStyle: TextStyle(
            fontSize: fontSize,
            fontWeight: FontWeight.bold,
            color: Colors.white,
          ),
        );
      }).toList();

    // ... Rest of your build method
    // Replace the PieChart with the following

    return PieChart(
      PieChartData(
        pieTouchData: PieTouchData(
          touchCallback: (FlTouchEvent event, pieTouchResponse) {
            setState(() {
              if (event is FlTapUpEvent && pieTouchResponse != null) {
                _touchedIndex = pieTouchResponse.touchedSection!.touchedSectionIndex;
              } else {
                _touchedIndex = null; // Reset touch on different event types if needed
              }
            });
          },
        ),
        // ... other properties
        sections: sections,
      ),
    );
  }
    Color _getRandomColor() {
    // This function should return a different color for each pie section.
    // For a production app, you might want to have a fixed set of colors or a more
    // sophisticated method of generating colors.
    return Color((Random().nextDouble() * 0xFFFFFF).toInt()).withOpacity(0.5);
  }
}