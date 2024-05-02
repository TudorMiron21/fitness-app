import 'package:fittnes_frontend/components/custom_divider.dart';
import 'package:flutter/material.dart';

class FilterResultsWidget extends StatefulWidget {
  final Function(String) onSearchChanged;
  final Function(RangeValues) onRangeChanged;

  const FilterResultsWidget({
    Key? key,
    required this.onSearchChanged,
    required this.onRangeChanged,
  }) : super(key: key);

  @override
  State<FilterResultsWidget> createState() => _FilterResultsWidgetState();
}

class _FilterResultsWidgetState extends State<FilterResultsWidget> {
  String searchText = '';
  RangeValues difficultyRange = const RangeValues(1.0, 3.0);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Search Bar
        Padding(
          padding: const EdgeInsets.all(8.0),
          child: TextField(
            decoration: InputDecoration(
              hintText: 'Search...',
              border: OutlineInputBorder(),
              prefixIcon: Icon(Icons.search),
            ),
            onChanged: (value) {
              setState(() {
                searchText = value;
              });
              widget.onSearchChanged(value);
            },
          ),
        ),

        // Range Slider for Difficulty
        Padding(
          padding: const EdgeInsets.all(8.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              CustomDivider(dividerText: "Select Difficulty Range"),
              RangeSlider(
                values: difficultyRange,
                min: 1.0,
                max: 3.0,
                divisions: 100,
                labels: RangeLabels(
                  difficultyRange.start
                      .toStringAsFixed(1), // Format to one decimal
                  difficultyRange.end.toStringAsFixed(1),
                ),
                onChanged: (RangeValues newRange) {
                  setState(() {
                    difficultyRange = newRange;
                  });
                  widget.onRangeChanged(newRange);
                },
              ),
            ],
          ),
        ),
      ],
    );
  }
}

class SearchPage extends StatefulWidget {
  const SearchPage({Key? key}) : super(key: key);

  @override
  State<SearchPage> createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  String searchQuery = '';
  RangeValues difficultyRange = const RangeValues(1, 10);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Search Page'),
      ),
      body: Column(
        children: [
          FilterResultsWidget(
            onSearchChanged: (String newSearch) {
              setState(() {
                searchQuery = newSearch;
              });
              // Update your search logic here
            },
            onRangeChanged: (RangeValues newRange) {
              setState(() {
                difficultyRange = newRange;
              });
              // Update your filter logic here
            },
          ),
          // Placeholder for search results
          Expanded(
            child: Center(
              child: Text("Results go here..."),
            ),
          ),
        ],
      ),
    );
  }
}
