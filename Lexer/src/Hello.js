outer: for (let i = 0; i < 3; i++) {

	for (let j = 0; j < 3; j++) {

		let input = prompt(`Coordinates (${i},${j})`, '');

		if (!input) break outer; // (*)

		// do something...
	}
}