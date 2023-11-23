import 'package:flutter/material.dart';

class _SetDialog extends StatefulWidget {
  final int? selectedSets;

  _SetDialog(this.selectedSets);

  @override
  _SetDialogState createState() => _SetDialogState();
}

class _SetDialogState extends State<_SetDialog> {
  late int? selectedSets;

  @override
  void initState() {
    super.initState();
    selectedSets = widget.selectedSets;
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text('Set Number of Sets'),
      content: Column(
        children: [
          Text('Choose the number of sets (1-12)'),
          SizedBox(height: 10),
          DropdownButton<int>(
            value: selectedSets,
            onChanged: (int? value) {
              setState(() {
                selectedSets = value;
              });
            },
            items: List.generate(12, (index) {
              return DropdownMenuItem<int>(
                value: index + 1,
                child: Text((index + 1).toString()),
              );
            }),
          ),
        ],
      ),
      actions: [
        TextButton(
          onPressed: () {
            Navigator.pop(context);
          },
          child: Text('Cancel'),
        ),
        TextButton(
          onPressed: () {
            Navigator.pop(context, selectedSets);
          },
          child: Text('Set'),
        ),
      ],
    );
  }
}
