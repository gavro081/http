import { useState } from "react";

interface Subject {
	id: number;
	name: string;
	code: string;
	abstract: string;
}

const Test = () => {
	const [responseData, setResponseData] = useState<Subject[]>([]);
	const [singleSubject, setSingleSubject] = useState<Subject | null>(null);
	const [message, setMessage] = useState<string>("");
	const [error, setError] = useState<string>("");
	const [loading, setLoading] = useState<string>("");

	// Input states
	const [limitInput, setLimitInput] = useState<string>("");
	const [codeInput, setCodeInput] = useState<string>("F23L2S032");
	const [idInput, setIdInput] = useState<string>("3");
	const [deleteCodeInput, setDeleteCodeInput] = useState<string>("FFGG081");
	const [deleteIdInput, setDeleteIdInput] = useState<string>("0");

	// Insert form states
	const [insertForm, setInsertForm] = useState({
		name: "Test Subject from frontend",
		code: "FFGG081",
		abstract: "lorem ipsum abstract",
	});

	// Update form states
	const [updateForm, setUpdateForm] = useState({
		id: "316",
		name: "Test Subject from frontend UPDATED",
		code: "FFGG081",
		abstract: "lorem ipsum abstract",
	});

	const clearResults = () => {
		setResponseData([]);
		setSingleSubject(null);
		setMessage("");
		setError("");
	};

	const scrollToTop = () => {
		window.scrollTo({ top: 0, behavior: "smooth" });
	};
	const fetchSubjects = async (limit?: string) => {
		try {
			clearResults();
			setLoading("Fetching subjects...");
			scrollToTop();
			const url =
				limit && limit.trim() !== ""
					? `api/subjects?limit=${limit}`
					: "api/subjects";
			const response = await fetch(url);

			if (!response.ok) {
				throw new Error(`HTTP ${response.status}: ${response.statusText}`);
			}

			const jsondata: { data: Subject[] } = await response.json();
			setResponseData(jsondata.data);
			setLoading("");
		} catch (err) {
			setError(
				`Error fetching subjects: ${
					err instanceof Error ? err.message : "Unknown error"
				}`
			);
			setLoading("");
		}
	};

	const fetchSingleSubjectByCode = async (code: string) => {
		try {
			if (!code || code.trim() === "") return;
			clearResults();
			setLoading("Fetching subject by code...");
			scrollToTop();
			const url = `api/subjects/code/${code}`;
			const response = await fetch(url);

			if (!response.ok) {
				throw new Error(`HTTP ${response.status}: ${response.statusText}`);
			}

			const jsondata: { data: Subject } = await response.json();
			setSingleSubject(jsondata.data);
			setLoading("");
		} catch (err) {
			setError(
				`Error fetching subject by code: ${
					err instanceof Error ? err.message : "Unknown error"
				}`
			);
			setLoading("");
		}
	};

	const fetchSingleSubjectById = async (id: string) => {
		try {
			if (!id || id.trim() === "") return;
			clearResults();
			setLoading("Fetching subject by ID...");
			scrollToTop();
			const url = `api/subjects/${id}`;
			const response = await fetch(url);

			if (!response.ok) {
				throw new Error(`HTTP ${response.status}: ${response.statusText}`);
			}

			const jsondata: { data: Subject } = await response.json();
			setSingleSubject(jsondata.data);
			setLoading("");
		} catch (err) {
			setError(
				`Error fetching subject by ID: ${
					err instanceof Error ? err.message : "Unknown error"
				}`
			);
			setLoading("");
		}
	};

	const insertSubject = async (subjectData: {
		name: string;
		code: string;
		abstract: string;
	}) => {
		try {
			clearResults();
			setLoading("Inserting subject...");
			scrollToTop();
			const url = `api/subjects`;
			const response = await fetch(url, {
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify(subjectData),
				method: "POST",
			});

			if (!response.ok) {
				throw new Error(`HTTP ${response.status}: ${response.statusText}`);
			}

			const jsondata: { message: string } = await response.json();
			setMessage(jsondata.message);
			setLoading("");
		} catch (err) {
			setError(
				`Error inserting subject: ${
					err instanceof Error ? err.message : "Unknown error"
				}`
			);
			setLoading("");
		}
	};

	const deleteByCode = async (code: string) => {
		try {
			if (!code || code.trim() === "") return;
			clearResults();
			setLoading("Deleting subject by code...");
			scrollToTop();
			const url = `api/subjects/code/${code}`;
			const response = await fetch(url, { method: "DELETE" });

			if (!response.ok) {
				throw new Error(`HTTP ${response.status}: ${response.statusText}`);
			}

			const jsondata: { data: Subject } = await response.json();
			setSingleSubject(jsondata.data);
			setMessage("Subject deleted successfully");
			setLoading("");
		} catch (err) {
			setError(
				`Error deleting subject by code: ${
					err instanceof Error ? err.message : "Unknown error"
				}`
			);
			setLoading("");
		}
	};

	const deleteById = async (id: string) => {
		try {
			if (!id || id.trim() === "") return;
			clearResults();
			setLoading("Deleting subject by ID...");
			scrollToTop();
			const url = `api/subjects/${id}`;
			const response = await fetch(url, { method: "DELETE" });

			if (!response.ok) {
				throw new Error(`HTTP ${response.status}: ${response.statusText}`);
			}

			const jsondata: { data: Subject } = await response.json();
			setSingleSubject(jsondata.data);
			setMessage("Subject deleted successfully");
			setLoading("");
		} catch (err) {
			setError(
				`Error deleting subject by ID: ${
					err instanceof Error ? err.message : "Unknown error"
				}`
			);
			setLoading("");
		}
	};

	const updateSubject = async (subjectData: {
		id: string;
		name: string;
		code: string;
		abstract: string;
	}) => {
		try {
			clearResults();
			setLoading("Updating subject...");
			scrollToTop();
			const payload = {
				id: parseInt(subjectData.id),
				abstract: subjectData.abstract,
				code: subjectData.code,
				name: subjectData.name,
			};
			const url = `api/subjects`;
			const response = await fetch(url, {
				headers: {
					"Content-Type": "application/json",
				},
				body: JSON.stringify(payload),
				method: "PUT",
			});

			if (!response.ok) {
				throw new Error(`HTTP ${response.status}: ${response.statusText}`);
			}

			const jsondata: { message: string } = await response.json();
			setMessage(jsondata.message);
			setLoading("");
		} catch (err) {
			setError(
				`Error updating subject: ${
					err instanceof Error ? err.message : "Unknown error"
				}`
			);
			setLoading("");
		}
	};

	// Component for displaying a single subject
	const SubjectCard = ({ subject }: { subject: Subject }) => (
		<div className="bg-gray-800 border border-gray-600 rounded-lg p-4 mb-4">
			<div className="flex justify-between items-start mb-2">
				<h3 className="text-lg font-semibold text-blue-400">{subject.name}</h3>
				<span className="text-sm text-gray-400 bg-gray-700 px-2 py-1 rounded">
					ID: {subject.id}
				</span>
			</div>
			<p className="text-gray-300 mb-1">
				<span className="font-medium">Code:</span> {subject.code}
			</p>
			<p className="text-gray-300">
				<span className="font-medium">Abstract:</span> {subject.abstract}
			</p>
		</div>
	);

	// Component for displaying loading state
	const LoadingSpinner = ({ message }: { message: string }) => (
		<div className="bg-blue-900 border border-blue-600 rounded-lg p-4 mb-4">
			<div className="flex items-center">
				<div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-400 mr-3"></div>
				<p className="text-blue-200">{message}</p>
			</div>
		</div>
	);

	// Component for displaying error messages
	const ErrorMessage = ({ error }: { error: string }) => (
		<div className="bg-red-900 border border-red-600 rounded-lg p-4 mb-4">
			<p className="text-red-200">{error}</p>
		</div>
	);

	// Component for displaying success messages
	const SuccessMessage = ({ message }: { message: string }) => (
		<div className="bg-green-900 border border-green-600 rounded-lg p-4 mb-4">
			<p className="text-green-200">{message}</p>
		</div>
	);

	return (
		<div className="min-h-screen py-8 bg-gray-900 text-gray-100 px-4">
			<div className="max-w-4xl mx-auto">
				<h1 className="text-3xl font-bold text-center mb-8 text-blue-400">
					Subject API Test Interface
				</h1>

				{/* Results Display Section */}
				<div className="mb-8">
					<h2 className="text-xl font-semibold mb-4 text-gray-200">Results</h2>
					<div className="bg-gray-800 border border-gray-600 rounded-lg p-4 min-h-32">
						{loading && <LoadingSpinner message={loading} />}
						{error && <ErrorMessage error={error} />}
						{message && <SuccessMessage message={message} />}

						{singleSubject && (
							<div>
								<h3 className="text-lg font-medium mb-3 text-gray-200">
									Single Subject Result:
								</h3>
								<SubjectCard subject={singleSubject} />
							</div>
						)}

						{responseData.length > 0 && (
							<div>
								<h3 className="text-lg font-medium mb-3 text-gray-200">
									Multiple Subjects ({responseData.length}):
								</h3>
								<div className="grid gap-3 max-h-96 overflow-y-auto">
									{responseData.map((subject, i) => (
										<SubjectCard key={i} subject={subject} />
									))}
								</div>
							</div>
						)}

						{!loading &&
							!error &&
							!message &&
							!singleSubject &&
							responseData.length === 0 && (
								<p className="text-gray-500 text-center py-8">
									Results will appear here...
								</p>
							)}
					</div>
				</div>

				{/* Clear Results Button */}
				<div className="mb-8 text-center">
					<button
						onClick={clearResults}
						className="px-4 py-2 bg-gray-700 hover:bg-gray-600 rounded-lg transition-colors"
					>
						Clear Results
					</button>
				</div>

				{/* API Testing Sections */}
				<div className="grid gap-8 md:grid-cols-2">
					{/* Fetch All Subjects Section */}
					<div className="bg-gray-800 border border-gray-600 rounded-lg p-6">
						<h3 className="text-lg font-semibold mb-4 text-green-400">
							Fetch All Subjects
						</h3>
						<div className="mb-4">
							<label className="block text-sm font-medium mb-2">
								Limit (optional):
							</label>
							<input
								type="number"
								value={limitInput}
								onChange={(e) => setLimitInput(e.target.value)}
								placeholder="Enter limit or leave empty for all"
								className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-400"
							/>
						</div>
						<button
							onClick={() => fetchSubjects(limitInput)}
							className="w-full py-2 bg-green-600 hover:bg-green-700 rounded-md transition-colors cursor-pointer"
						>
							Fetch Subjects
						</button>
					</div>

					{/* Fetch Subject by Code Section */}
					<div className="bg-gray-800 border border-gray-600 rounded-lg p-6">
						<h3 className="text-lg font-semibold mb-4 text-blue-400">
							Fetch Subject by Code
						</h3>
						<div className="mb-4">
							<label className="block text-sm font-medium mb-2">
								Subject Code:
							</label>
							<input
								type="text"
								value={codeInput}
								onChange={(e) => setCodeInput(e.target.value)}
								placeholder="Enter subject code"
								className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-400"
							/>
						</div>
						<button
							onClick={() => fetchSingleSubjectByCode(codeInput)}
							className="w-full py-2 bg-blue-600 hover:bg-blue-700 rounded-md transition-colors cursor-pointer"
						>
							Fetch by Code
						</button>
					</div>

					{/* Fetch Subject by ID Section */}
					<div className="bg-gray-800 border border-gray-600 rounded-lg p-6">
						<h3 className="text-lg font-semibold mb-4 text-blue-400">
							Fetch Subject by ID
						</h3>
						<div className="mb-4">
							<label className="block text-sm font-medium mb-2">
								Subject ID:
							</label>
							<input
								type="text"
								value={idInput}
								onChange={(e) => setIdInput(e.target.value)}
								placeholder="Enter subject ID"
								className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-400"
							/>
						</div>
						<button
							onClick={() => fetchSingleSubjectById(idInput)}
							className="w-full py-2 bg-blue-600 hover:bg-blue-700 rounded-md transition-colors cursor-pointer"
						>
							Fetch by ID
						</button>
					</div>

					{/* Insert Subject Section */}
					<div className="bg-gray-800 border border-gray-600 rounded-lg p-6">
						<h3 className="text-lg font-semibold mb-4 text-purple-400">
							Insert New Subject
						</h3>
						<div className="space-y-4 mb-4">
							<div>
								<label className="block text-sm font-medium mb-2">Name:</label>
								<input
									type="text"
									value={insertForm.name}
									onChange={(e) =>
										setInsertForm({ ...insertForm, name: e.target.value })
									}
									placeholder="Enter subject name"
									className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-400"
								/>
							</div>
							<div>
								<label className="block text-sm font-medium mb-2">Code:</label>
								<input
									type="text"
									value={insertForm.code}
									onChange={(e) =>
										setInsertForm({ ...insertForm, code: e.target.value })
									}
									placeholder="Enter subject code"
									className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-400"
								/>
							</div>
							<div>
								<label className="block text-sm font-medium mb-2">
									Abstract:
								</label>
								<textarea
									value={insertForm.abstract}
									onChange={(e) =>
										setInsertForm({ ...insertForm, abstract: e.target.value })
									}
									placeholder="Enter subject abstract"
									rows={3}
									className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-400"
								/>
							</div>
						</div>
						<button
							onClick={() => insertSubject(insertForm)}
							className="w-full py-2 bg-purple-600 hover:bg-purple-700 rounded-md transition-colors cursor-pointer"
						>
							Insert Subject
						</button>
					</div>

					{/* Update Subject Section */}
					<div className="bg-gray-800 border border-gray-600 rounded-lg p-6">
						<h3 className="text-lg font-semibold mb-4 text-yellow-400">
							Update Subject
						</h3>
						<div className="space-y-4 mb-4">
							<div>
								<label className="block text-sm font-medium mb-2">ID:</label>
								<input
									type="text"
									value={updateForm.id}
									onChange={(e) =>
										setUpdateForm({ ...updateForm, id: e.target.value })
									}
									placeholder="Enter subject ID"
									className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-400"
								/>
							</div>
							<div>
								<label className="block text-sm font-medium mb-2">Name:</label>
								<input
									type="text"
									value={updateForm.name}
									onChange={(e) =>
										setUpdateForm({ ...updateForm, name: e.target.value })
									}
									placeholder="Enter subject name"
									className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-400"
								/>
							</div>
							<div>
								<label className="block text-sm font-medium mb-2">Code:</label>
								<input
									type="text"
									value={updateForm.code}
									onChange={(e) =>
										setUpdateForm({ ...updateForm, code: e.target.value })
									}
									placeholder="Enter subject code"
									className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-400"
								/>
							</div>
							<div>
								<label className="block text-sm font-medium mb-2">
									Abstract:
								</label>
								<textarea
									value={updateForm.abstract}
									onChange={(e) =>
										setUpdateForm({ ...updateForm, abstract: e.target.value })
									}
									placeholder="Enter subject abstract"
									rows={3}
									className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-400"
								/>
							</div>
						</div>
						<button
							onClick={() => updateSubject(updateForm)}
							className="w-full py-2 bg-yellow-600 hover:bg-yellow-700 rounded-md transition-colors cursor-pointer"
						>
							Update Subject
						</button>
					</div>

					{/* Delete Subject by Code Section */}
					<div className="bg-gray-800 border border-gray-600 rounded-lg p-6">
						<h3 className="text-lg font-semibold mb-4 text-red-400">
							Delete Subject by Code
						</h3>
						<div className="mb-4">
							<label className="block text-sm font-medium mb-2">
								Subject Code:
							</label>
							<input
								type="text"
								value={deleteCodeInput}
								onChange={(e) => setDeleteCodeInput(e.target.value)}
								placeholder="Enter subject code"
								className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-400"
							/>
						</div>
						<button
							onClick={() => deleteByCode(deleteCodeInput)}
							className="w-full py-2 bg-red-600 hover:bg-red-700 rounded-md transition-colors cursor-pointer"
						>
							Delete by Code
						</button>
					</div>

					{/* Delete Subject by ID Section */}
					<div className="bg-gray-800 border border-gray-600 rounded-lg p-6">
						<h3 className="text-lg font-semibold mb-4 text-red-400">
							Delete Subject by ID
						</h3>
						<div className="mb-4">
							<label className="block text-sm font-medium mb-2">
								Subject ID:
							</label>
							<input
								type="text"
								value={deleteIdInput}
								onChange={(e) => setDeleteIdInput(e.target.value)}
								placeholder="Enter subject ID"
								className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-md text-white placeholder-gray-400"
							/>
						</div>
						<button
							onClick={() => deleteById(deleteIdInput)}
							className="w-full py-2 bg-red-600 hover:bg-red-700 rounded-md transition-colors cursor-pointer"
						>
							Delete by ID
						</button>
					</div>
				</div>
			</div>
		</div>
	);
};

export default Test;
