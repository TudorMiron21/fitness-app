import 'package:fittnes_frontend/components/filter_results_widget.dart';
import 'package:flutter/material.dart';

class SearchPage extends StatefulWidget {
  const SearchPage({super.key});

  @override
  State<SearchPage> createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  RangeValues difficultyRange = RangeValues(1.0, 3.0);

  String searchValue = "";

  dynamic onSearchChanged(String newText) {
    setState(() {
      searchValue = newText;
      print(searchValue);
    });
  }

  void onRangeChanged(RangeValues newRange) {
    setState(() {
      difficultyRange = newRange;
      print(difficultyRange);
    });
  }

  @override
  Widget build(BuildContext context) {
    return FilterResultsWidget(
        onSearchChanged: onSearchChanged, onRangeChanged: onRangeChanged);
  }
}
