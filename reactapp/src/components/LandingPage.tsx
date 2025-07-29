const LandingPage = () => {
	return (
		<div className="min-h-screen bg-gray-900 text-gray-100 flex flex-col items-center justify-center px-4">
			<div className="max-w-2xl mx-auto text-center space-y-8">
				<div className="space-y-4">
					<h1 className="text-4xl md:text-5xl font-bold text-white">
						Custom HTTP Server
					</h1>
					<p className="text-xl text-gray-400">
						This React application is being served from a custom Java HTTP
						server
					</p>
				</div>

				<div className="flex items-center justify-center space-x-2 bg-gray-800 px-6 py-3 rounded-lg border border-gray-700">
					<div className="w-3 h-3 bg-green-400 rounded-full animate-pulse"></div>
					<span className="text-sm font-medium text-gray-300">
						Server Active
					</span>
				</div>

				{/*<div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-12">*/}
				{/*    <div className="bg-gray-800 p-6 rounded-lg border border-gray-700">*/}
				{/*        <div className="flex items-center space-x-3 mb-3">*/}
				{/*            <Code className="w-6 h-6 text-purple-400" />*/}
				{/*            <h3 className="text-lg font-semibold">Custom Built</h3>*/}
				{/*        </div>*/}
				{/*        <p className="text-gray-400 text-sm">*/}
				{/*            Powered by a handcrafted Java HTTP server implementation*/}
				{/*        </p>*/}
				{/*    </div>*/}

				{/*    <div className="bg-gray-800 p-6 rounded-lg border border-gray-700">*/}
				{/*        <div className="flex items-center space-x-3 mb-3">*/}
				{/*            <Zap className="w-6 h-6 text-yellow-400" />*/}
				{/*            <h3 className="text-lg font-semibold">Local Hosting</h3>*/}
				{/*        </div>*/}
				{/*        <p className="text-gray-400 text-sm">*/}
				{/*            Running locally with full control over server behavior*/}
				{/*        </p>*/}
				{/*    </div>*/}
				{/*</div>*/}
			</div>
		</div>
	);
};

export default LandingPage;
