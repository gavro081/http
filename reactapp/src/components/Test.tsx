import { useState } from "react";

const Test = () => {
	const [responseData, setResponseData] = useState("");
	const handleClick = () => {
		const testFetch = async () => {
			try {
				const data = await fetch("/api/something/");
				const jsondata: { data: string } = await data.json();
				console.log(jsondata.data);
				setResponseData(jsondata.data);
			} catch (err) {
				console.error(err);
			}
		};
		testFetch();
	};
	return (
		<div className="min-h-screen bg-gray-900 text-gray-100 flex flex-col items-center justify-center px-4 gap-4">
			<button
				className="p-3 bg-gray-700 rounded-xl cursor-pointer"
				onClick={handleClick}
			>
				Send request to server
			</button>
			<p className="p-5 rounded-xl border border-gray-500">
				{responseData === ""
					? "Response from server will appear here"
					: `Server says: ${responseData}`}
			</p>
		</div>
	);
};

export default Test;
