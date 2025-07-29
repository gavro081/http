import { useState } from "react";

interface Subject {
	name: string;
	code: string;
	abstract: string;
}

const Test = () => {
	const [responseData, setResponseData] = useState<Subject[]>([]);
	const handleClick = () => {
		const testFetch = async () => {
			try {
				const data = await fetch("/api/something/");
				const jsondata: { data: Subject[] } = await data.json();
				console.log(jsondata.data);
				setResponseData(jsondata.data);
			} catch (err) {
				console.error(err);
			}
		};
		testFetch();
	};
	return (
		<div className="min-h-screen py-5 bg-gray-900 text-gray-100 flex flex-col items-center justify-center px-4 gap-4">
			<button
				className="p-3 bg-gray-700 rounded-xl cursor-pointer"
				onClick={handleClick}
			>
				Send request for 5 subjects to server.
			</button>
			<div className="w-full mx-3 rounded-xl border border-gray-500 p-3">
				{responseData.length === 0 ? (
					"Response from server will appear here"
				) : (
					<ul>
						{responseData.map((subject, i) => {
							return (
								<li>
									<p>
										{i}. {subject.name}
									</p>
									<p>{subject.code}</p>
									<p>{subject.abstract}</p>
								</li>
							);
						})}
					</ul>
				)}
			</div>
		</div>
	);
};

export default Test;
